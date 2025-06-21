package com.github.tatercertified.cmp.backend;

import com.github.tatercertified.cmp.api.PartitionManagerConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public final class PartitionManager {
    private final Map<String, Partition> partitions = new LinkedHashMap<>();
    private final PartitionManagerConfig config;

    public PartitionManager(PartitionManagerConfig config) {
        this.config = config;
    }

    public void tickPartitions() {
        if (this.config.threadedPartitions()) {
            partitions.values().parallelStream().forEach(partition -> {
                // TODO Tick each world
            });
        }
    }
}
