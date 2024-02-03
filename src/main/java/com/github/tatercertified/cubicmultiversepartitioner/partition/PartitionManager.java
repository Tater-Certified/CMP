package com.github.tatercertified.cubicmultiversepartitioner.partition;

import com.github.tatercertified.cubicmultiversepartitioner.api.CMPAPI;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.fantasy.Fantasy;

import java.util.HashMap;
import java.util.Map;

public class PartitionManager {
    private final MinecraftServer server;
    private final NbtList partitions;
    public static Fantasy fantasy;
    private final Map<Identifier, PartitionWorld> cmpWorlds = new HashMap<>();
    /**
     * Initializes the PartitionManager
     * @param server MinecraftServer
     * @param partitions NbtList from PersistentState
     */
    public PartitionManager(MinecraftServer server, NbtList partitions) {
        this.server = server;
        this.partitions = partitions;
        fantasy = Fantasy.get(server);
    }

    /**
     * Loads all PartitionWorlds
     */
    public void loadWorlds() {
        for (int i = 0; i < partitions.size(); i++) {
            NbtCompound data = partitions.getCompound(i);
            PartitionWorld world = unpack(data);
            cmpWorlds.put(world.identifier(), world);
            CMPAPI.WorldLoadedEvent.WORLD_LOAD_EVENT.invoker().runWorldLoadEvent(world);
        }
    }

    /**
     * Loads the specified PartitionWorld
     * @param identifier Identifier of the PartitionWorld
     */
    public void loadWorld(Identifier identifier) {
        for (int i = 0; i < partitions.size(); i++) {
            NbtCompound data = partitions.getCompound(i);
            if (data.getString("path").equals(identifier.getPath())) {
                PartitionWorld world = unpack(data);
                cmpWorlds.put(world.identifier(), world);
                CMPAPI.WorldLoadedEvent.WORLD_LOAD_EVENT.invoker().runWorldLoadEvent(world);
                break;
            }
        }
    }

    private PartitionWorld unpack(NbtCompound compound) {
        return new PartitionWorld(
                new Identifier(compound.getString("namespace"),
                compound.getString("path")),
                compound.getLong("seed"),
                compound.getInt("worldBorderSize"),
                unpackWorlds(compound),
                compound.getBoolean("tickSeparately"),
                server
        );
    }

    private Identifier[] unpackWorlds(NbtCompound compound) {
        NbtList list = compound.getList("dimensions", NbtElement.LIST_TYPE);
        Identifier[] identifiers = new Identifier[list.size()];
        for (int i = 0; i < list.size(); i++) {
            identifiers[i] = new Identifier(((NbtList)list.get(i)).getString(0), ((NbtList)list.get(i)).getString(1));
        }
        return identifiers;
    }

    /**
     * Creates a new PartitionWorld
     * @param identifier Identifier for the PartitionWorld
     * @param seed Seed of Partition
     * @param worldBorderSize Radius of the world border
     * @param dimensions Dimension Identifiers (works with modded as well)
     * @param tickSeparately Whether to tick separate from the main 3 dimensions. This will cause desyncs if enabled!
     */
    public void createWorld(Identifier identifier, long seed, int worldBorderSize, Identifier[] dimensions, boolean tickSeparately) {
        System.out.println("TEST");
        PartitionWorld world = new PartitionWorld(identifier, seed, worldBorderSize, dimensions, tickSeparately, this.server);
        System.out.println("TEST");
        cmpWorlds.put(identifier, world);
        System.out.println("TEST");
        CMPAPI.WorldCreatedEvent.WORLD_CREATED_EVENT.invoker().runWorldCreatedEvent(world);
        System.out.println("TEST");
    }

    /**
     * Unloads and removes the specified PartitionWorld
     * @param identifier Identifier for the PartitionWorld
     */
    public void removeWorld(Identifier identifier) {
        PartitionWorld world = this.getWorld(identifier);
        world.discardDimensions();
        cmpWorlds.remove(identifier);
    }

    /**
     * Saves all the PartitionWorlds to the CMPPersistentState
     */
    public void saveAll() {
        NbtList partitions = new NbtList();
        for (PartitionWorld world : cmpWorlds.values()) {
            partitions.add(world.pack());
        }
        CMPPersistentState state = CMPPersistentState.getServerState(server);
        state.partitions = partitions;
        state.markDirty();
    }

    /**
     * Gathers the PartitionWorld from the HashMap of all registered PartitionWorlds
     * @param identifier Identifier to search for
     * @return PartitionWorld that matches the "identifier"
     */
    public PartitionWorld getWorld(Identifier identifier) {
        return cmpWorlds.get(identifier);
    }

    /**
     * Gets how many PartitionWorlds are loaded
     * @return Integer of the amount of PartitionWorlds in "cmpWorlds" HashMap
     */
    public int getWorldCount() {
        return cmpWorlds.size();
    }

    /**
     * Teleports an Entity to a Dimension in the specified PartitionWorld
     * @param identifier Identifier for the PartitionWorld
     * @param dimension Identifier for the Dimension (defaults to the first Dimensions if null)
     * @param entity Entity being teleported
     * @param destination Vec3d destination coordinates
     */
    public void teleportToWorld(Identifier identifier, @Nullable Identifier dimension, Entity entity, Vec3d destination) {
        PartitionWorld world = getWorld(identifier);
        TeleportTarget teleportTarget = new TeleportTarget(destination, new Vec3d(0, 0, 0), 0.0F, 0.0F);
        FabricDimensions.teleport(entity, dimension == null ? world.getWorld(world.dimensions()[0]) : world.getWorld(dimension), teleportTarget);
    }

    /**
     * Gets the PartitionWorld of a child Dimension
     * @param serverWorld ServerWorld of the Dimension
     * @return PartitionWorld of "serverWorld"
     */
    public PartitionWorld getPartitionWorld(ServerWorld serverWorld) {
        return this.getWorld(((ServerWorldPartitionInterface)serverWorld).getParentIdentifier());
    }
}
