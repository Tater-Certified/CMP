package com.github.tatercertified.cmp.backend;

import com.github.tatercertified.cmp.CMP;
import com.github.tatercertified.cmp.api.PartitionManagerConfig;
import com.github.tatercertified.cmp.api.PartitionWorldConfig;
import com.github.tatercertified.cmp.backend.data.PartitionWorldData;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.world.GameRules;
import org.jetbrains.annotations.Nullable;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldHandle;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public final class PartitionManager {
    private final Map<UUID, PartitionWorld> activePartitions = new LinkedHashMap<>();
    private final PartitionManagerConfig config;

    public PartitionManager(PartitionManagerConfig config) {
        this.config = config;
    }

    public void tickPartitions() {
        if (this.config.threadedPartitions()) {
            activePartitions.values().parallelStream().forEach(partition -> {
                // TODO Tick each world
            });
        }
    }

    public PartitionWorld addPartition(PartitionWorldConfig config, @Nullable UUID owner) {
        Map<String, PartitionDimension> dimensionMap = new HashMap<>();

        UUID uuid = UUID.randomUUID();
        long seed = ThreadLocalRandom.current().nextLong();

        for (String dimension : config.dimensions()) {
            RuntimeWorldConfig worldConfig = CMPRegistry.get(dimension);
            worldConfig.setSeed(seed);

            for (Pair<GameRules.Key<GameRules.BooleanRule>, Boolean> booleanRule : config.defaultBoolGameRules()) {
                worldConfig.setGameRule(booleanRule.getLeft(), booleanRule.getRight());
            }
            for (Pair<GameRules.Key<GameRules.IntRule>, Integer> intRule : config.defaultIntGameRules()) {
                worldConfig.setGameRule(intRule.getLeft(), intRule.getRight());
            }

            RuntimeWorldHandle runtimeDimension = CMP.FANTASY.getOrOpenPersistentWorld(Identifier.of(uuid + "_" + dimension), worldConfig);
            runtimeDimension.setTickWhenEmpty(false);
            dimensionMap.put(dimension, new PartitionDimension(runtimeDimension));
        }

        PartitionWorld world = new PartitionWorld(dimensionMap, uuid, owner, seed);
        openPartition(world);
        return world;
    }

    public PartitionWorld loadPartition(PartitionWorldData data) {
        Map<String, PartitionDimension> dimensionMap = new HashMap<>();

        for (String dimension : data.getDimensions()) {
            RuntimeWorldConfig worldConfig = CMPRegistry.get(dimension);
            worldConfig.setSeed(data.getSeed());

            RuntimeWorldHandle runtimeDimension = CMP.FANTASY.getOrOpenPersistentWorld(Identifier.of(data.getUuid() + "_" + dimension), worldConfig);
            runtimeDimension.setTickWhenEmpty(false);
            dimensionMap.put(dimension, new PartitionDimension(runtimeDimension));
        }

        PartitionWorld world = new PartitionWorld(dimensionMap, data.getUuid(), data.getOwner(), data.getSeed());
        openPartition(world);
        return world;
    }

    public void openPartition(PartitionWorld world) {
        activePartitions.put(world.uuid(), world);
    }

    public void deletePartition(PartitionWorld world) {
        activePartitions.remove(world.uuid());
        for (PartitionDimension dimension : world.DIMENSIONS().values()) {
            dimension.dimension().delete();
        }

        // TODO Remove the PartitionWorldData as well
    }

    public void unloadPartition(PartitionWorld world) {
        activePartitions.remove(world.uuid());
        for (PartitionDimension dimension : world.DIMENSIONS().values()) {
            dimension.dimension().unload();
        }
    }
}
