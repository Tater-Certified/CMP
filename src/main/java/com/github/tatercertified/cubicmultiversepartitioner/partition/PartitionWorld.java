package com.github.tatercertified.cubicmultiversepartitioner.partition;

import com.github.tatercertified.cubicmultiversepartitioner.api.CMPAPI;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;
import xyz.nucleoid.fantasy.util.VoidChunkGenerator;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * This is the main class for creating dimensions and storing information about those dimensions
 * @param identifier The Identifier for the PartitionWorld
 * @param seed The seed for each dimension
 * @param worldBorderSize Limits the radius to this amount
 * @param dimensions Vanilla (or modded) dimension Identifiers that your dimensions will inherit
 * @param tickSeparately Whether to tick independent of the main 3 dimensions of the server (this will cause time syncing issues, but will allow these dimensions to lag without affecting the rest of the server)
 * @param server MinecraftServer instance
 */
public record PartitionWorld(Identifier identifier, long seed, int worldBorderSize, Identifier[] dimensions, boolean tickSeparately, MinecraftServer server) {

    private static final Map<Identifier, RuntimeWorldHandle> worlds = new HashMap<>();
    private static ExecutorService tickThread;

    public PartitionWorld {
        if (tickSeparately()) {
            tickThread = Executors.newSingleThreadExecutor();
        }
        setUpWorlds();
    }

    private void setUpWorlds() {
        for (Identifier id : dimensions()) {
            DimensionOptions options = server.getRegistryManager().get(RegistryKeys.DIMENSION).get(id);
            ChunkGenerator generator;
            if (options != null) {
                generator = options.chunkGenerator();
            } else {
                options = server.getRegistryManager().get(RegistryKeys.DIMENSION).get(DimensionOptions.OVERWORLD);
                generator = new VoidChunkGenerator(server.getRegistryManager().get(RegistryKeys.BIOME));
            }

            RuntimeWorldConfig config = new RuntimeWorldConfig()
                    .setSeed(this.seed)
                    .setShouldTickTime(true)
                    .setDimensionType(options.dimensionTypeEntry())
                    .setGenerator(generator);

            RuntimeWorldHandle handle = PartitionManager.fantasy.getOrOpenPersistentWorld(identifier(), config);
            worlds.put(id, handle);

            handle.setTickWhenEmpty(false);

            handle.asWorld().getWorldBorder().setCenter(0.5, 0.5);
            handle.asWorld().getWorldBorder().setSize(worldBorderSize());

            ((ServerWorldPartitionInterface)handle.asWorld()).setParentIdentifier(identifier());
        }
    }

    /**
     * Searches the PartitionWorld for a specific dimension
     * @param identifier Identifier for the dimension
     * @return ServerWorld of the dimension
     */
    public ServerWorld getWorld(Identifier identifier) {
        return worlds.get(identifier).asWorld();
    }

    /**
     * Removes all dimensions in this PartitionWorld without saving
     */
    public void discardDimensions() {
        worlds.forEach((s, runtimeWorldHandle) -> runtimeWorldHandle.delete());
        CMPAPI.WorldRemovedEvent.WORLD_REMOVED_EVENT.invoker().runWorldRemovedEvent(this);
    }

    /**
     * Converts all the data into NBT
     * @return PartitionWorld as NbtCompound
     */
    public NbtCompound pack() {
        NbtCompound compound = new NbtCompound();
        compound.putString("namespace", identifier().getNamespace());
        compound.putString("path", identifier().getPath());
        compound.putLong("seed", seed());
        compound.putInt("worldBorderSize", worldBorderSize());
        compound.putBoolean("tickSeparately", tickSeparately());
        compound.put("dimensions", packWorlds());
        CMPAPI.WorldSavedEvent.WORLD_SAVED_EVENT.invoker().runWorldSavedEvent(this);
        return compound;
    }

    private NbtList packWorlds() {
        NbtList worlds = new NbtList();
        for (Identifier id : dimensions()) {
            NbtList worldIdentifier = new NbtList();
            worldIdentifier.add(0, NbtString.of(id.getNamespace()));
            worldIdentifier.add(1, NbtString.of(id.getPath()));
            worlds.add(worldIdentifier);
        }
        return worlds;
    }

    /**
     * Ticks all the Dimensions.
     * If "tickSeparately" is true, the Dimensions in this PartitionWorld are ticked on a separate thread
     */
    public void tick() {
        if (tickSeparately()) {
            tickThread.execute(this::tickWorlds);
        } else {
            tickWorlds();
        }
    }

    private void tickWorlds() {
        worlds.forEach((identifier1, runtimeWorldHandle) -> {
            CMPAPI.WorldTickedEvent.WORLD_TICKED_EVENT.invoker().runWorldTickedEvent(runtimeWorldHandle.asWorld(), this);
            runtimeWorldHandle.asWorld().tick(() -> true);
        });
    }
}
