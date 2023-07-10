package com.github.tatercertified.cubicmultiversepartitioner.partition;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.FlatLevelGeneratorPreset;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.chunk.FlatChunkGenerator;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.fantasy.Fantasy;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;
import xyz.nucleoid.fantasy.util.VoidChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PartitionWorld {
    private final MinecraftServer server;
    private final Fantasy fantasy;
    private long id;
    private String name;
    private long seed;
    private int size;
    private int[] dimensions;
    private String generator;
    private final List<RuntimeWorldHandle> worldHandles = new ArrayList<>();
    private boolean hasNether;
    private boolean hasEnd;

    public PartitionWorld(MinecraftServer server, Fantasy fantasy, NbtCompound nbt) {
        this.server = server;
        this.fantasy = fantasy;
        unpackNbt(nbt);
        setUpWorlds(this.generator);
    }

    /**
     * Sets up the PartitionWorld for active usage
     * @param nbt Nbt Data from the PersistentState
     */
    private void unpackNbt(NbtCompound nbt) {
        this.id = nbt.getLong("id");
        this.name = nbt.getString("name");
        this.seed = nbt.getLong("seed");
        this.size = nbt.getInt("size");
        this.dimensions = nbt.getIntArray("dimensions");
        this.generator = nbt.getString("generator");
    }

    /**
     * Creates the physical world
     */
    private void setUpWorlds(@Nullable String generatorType) {
        ChunkGenerator generator = null;
        if (generatorType != null) {
            switch (generatorType) {
                case "superflat" -> {
                    FlatLevelGeneratorPreset preset = server.getRegistryManager().get(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET).get(FlatLevelGeneratorPresets.CLASSIC_FLAT);
                    generator = new FlatChunkGenerator(preset.settings());
                }
                case "minimalsuperflat" -> {

                }
                case "void" -> generator = new VoidChunkGenerator(server.getRegistryManager().get(RegistryKeys.BIOME).getEntry(0).get());
            }
        }

        RegistryKey<DimensionType> type = null;
        for (int i : dimensions) {
            switch (i) {
                case 1 -> {
                    type = DimensionTypes.OVERWORLD;
                    if (generator == null) {
                        generator = server.getOverworld().getChunkManager().getChunkGenerator();
                    }
                }
                case 2 -> {
                    type = DimensionTypes.THE_NETHER;
                    if (generator == null) {
                        generator = Objects.requireNonNull(server.getWorld(World.NETHER)).getChunkManager().getChunkGenerator();
                    }
                    hasNether = true;
                }
                case 3 -> {
                    type = DimensionTypes.THE_END;
                    if (generator == null) {
                        generator = Objects.requireNonNull(server.getWorld(World.END)).getChunkManager().getChunkGenerator();
                    }
                    hasEnd = true;
                }
            }

            RuntimeWorldConfig config = new RuntimeWorldConfig()
                    .setSeed(this.seed)
                    .setShouldTickTime(true)
                    .setDimensionType(type)
                    .setGenerator(generator);

            Identifier identifier;
            if (i == 0) {
                identifier = new Identifier(CMP.MODID, Long.toString(id));
            } else {
                identifier = new Identifier(CMP.MODID, id + "_" + i);
            }

            RuntimeWorldHandle handle = fantasy.getOrOpenPersistentWorld(identifier, config);
            worldHandles.add(i, handle);

            handle.setTickWhenEmpty(false);

            handle.asWorld().getWorldBorder().setCenter(0.5, 0.5);
            handle.asWorld().getWorldBorder().setSize(this.size);
        }
    }

    /**
     * Saves all data to a NbtCompound
     * @return Saved data as NbtCompound
     */
    public NbtCompound packNbt() {
        NbtCompound compound = new NbtCompound();
        compound.putLong("id", this.id);
        compound.putString("name", this.name);
        compound.putLong("seed", this.seed);
        compound.putInt("size", this.size);
        compound.putIntArray("dimensions", this.dimensions);
        compound.putString("generator", this.generator);

        return compound;
    }



    // Data related methods

    /**
     * Grabs the seed
     * @return Seed
     */
    public long getSeed() {
        return this.seed;
    }

    /**
     * Grabs the ID of the Partition
     * @return ID of PartitionWorld
     */
    public long getId() {
        return this.id;
    }

    /**
     * Grabs the Name of the Partition
     * @return Name of PartitionWorld
     */
    public String getName() {
        return this.name;
    }

    /**
     * Grabs the PartitionWorld size
     * @return Size of WorldBorder
     */
    public int getWorldSize() {
        return this.size;
    }

    /**
     * Gets the Dimension as a ServerWorld.
     * Remember that 1 = Overworld, 2 = Nether, and 3 = End
     * @param dimension Corresponding number that matches the Dimension
     * @return ServerWorld of the Dimension. If it doesn't exist, returns null
     */
    @Nullable
    public ServerWorld getDimension(int dimension) {
        if (this.worldHandles.size() > dimension && this.worldHandles.get(dimension) != null) {
            return this.worldHandles.get(dimension).asWorld();
        } else {
            return null;
        }
    }

    /**
     * If the Partition contains a Nether
     * @return if it has a Nether
     */
    public boolean hasNether() {
        return this.hasNether;
    }

    /**
     * If the Partition contains an End
     * @return if it has an End
     */
    public boolean hasEnd() {
        return this.hasEnd;
    }

    /**
     * Gets the RuntimeWorldHandles for the specified Dimension
     * Remember that 1 = Overworld, 2 = Nether, and 3 = End
     * @param dimension Corresponding number that matches the Dimension
     * @return RuntimeWorldHandle for Dimension
     */
    @Nullable
    public RuntimeWorldHandle getDimensionHandles(int dimension) {
        if (this.worldHandles.size() > dimension && this.worldHandles.get(dimension) != null) {
            return worldHandles.get(dimension);
        } else {
            return null;
        }
    }

    /**
     * Gets all RuntimeWorldHandles
     * @return All RuntimeWorldHandles
     */
    public List<RuntimeWorldHandle> getAllDimensionHandles() {
        return this.worldHandles;
    }

    /**
     * Sets a Boolean GameRule for the PartitionWorld
     * @param gameRule Boolean GameRule
     * @param bool boolean
     */
    public void setGameRule(GameRules.Key<GameRules.BooleanRule> gameRule, boolean bool) {
        for (RuntimeWorldHandle handle : worldHandles) {
            handle.asWorld().getGameRules().get(gameRule).set(bool, server);
        }
    }

    /**
     * Sets an Integer GameRule for the PartitionWorld
     * @param gameRule Integer GameRule
     * @param integer int
     */
    public void setGameRule(GameRules.Key<GameRules.IntRule> gameRule, int integer) {
        for (RuntimeWorldHandle handle : worldHandles) {
            handle.asWorld().getGameRules().get(gameRule).set(integer, server);
        }
    }
}
