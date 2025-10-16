package com.github.tatercertified.cmp.backend.data;

import java.util.Set;
import java.util.UUID;

// TODO Save this somewhere
public class PartitionWorldData {
    private long seed;
    private UUID uuid;
    private UUID owner;
    private Set<String> dimensions;

    public PartitionWorldData(long seed, UUID uuid, UUID owner, Set<String> dimensions) {
        this.seed = seed;
        this.uuid = uuid;
        this.owner = owner;
        this.dimensions = dimensions;
    }

    public long getSeed() {
        return this.seed;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public UUID getOwner() {
        return this.owner;
    }

    public Set<String> getDimensions() {
        return this.dimensions;
    }
}
