package com.github.tatercertified.cubicmultiversepartitioner.partition;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import net.fabricmc.fabric.api.dimension.v1.FabricDimensions;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.List;
import java.util.Objects;

public class PartitionFunctions {
    /**
     * Teleports an Entity to the specified PartitionWorld's Overworld
     * @param world PartitionWorld
     * @param target Entity to be teleported
     */
    public static void teleportToPartition(PartitionWorld world, Entity target) {
        TeleportTarget teleportTarget = new TeleportTarget(new Vec3d(0, 0, 0), new Vec3d(0, 0, 0), 0.0F, 0.0F);
        FabricDimensions.teleport(target, world.getDimension(1), teleportTarget);
    }

    /**
     * Unloads all Dimensions in a Partition
     * @param world PartitionWorld
     */
    public static void unloadPartition(PartitionWorld world) {
        List<RuntimeWorldHandle> handles = world.getAllDimensionHandles();
        for (RuntimeWorldHandle handle : handles) {
            handle.unload();
        }
    }

    /**
     * Deletes a Partition
     * @param world PartitionWorld
     */
    public static void deletePartition(PartitionWorld world) {
        List<RuntimeWorldHandle> handles = world.getAllDimensionHandles();
        for (RuntimeWorldHandle handle : handles) {
            handle.delete();
        }
    }

    // TODO Find a good way to get ServerWorld <-> Partition communication
    // TODO This will probably include making an interface in ServerWorldMixin and making a variable instance of the PartitionWorld in the ServerWorld for easy access
    @Nullable
    public static PartitionWorld getPartitionFromServerWorld(ServerWorld world) {
        String path = world.getRegistryKey().getValue().getNamespace();
        if (!Objects.equals(path, CMP.MODID)) {
            return null;
        }
        long name = Long.getLong(world.getRegistryKey().getValue().getPath());
    }
}
