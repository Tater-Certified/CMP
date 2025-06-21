package com.github.tatercertified.cmp.api;

public interface PartitionManagerConfig {
    default boolean threadedPartitions() {
        return false;
    }

    default boolean disableThreadTickSync() {
        return false;
    }

    default boolean threadedWorlds() {
        return false;
    }

    default boolean generateVanillaWorlds() {
        return true;
    }
}
