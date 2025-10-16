package com.github.tatercertified.cmp.backend;

import com.github.tatercertified.cmp.CMP;
import com.github.tatercertified.cmp.backend.data.PartitionWorldData;
import com.github.tatercertified.cmp.backend.utils.TraderSpawnerAccessor;
import com.github.tatercertified.cmp.backend.utils.WanderingTraderSpawner;
import net.minecraft.util.Identifier;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;

import java.util.Map;
import java.util.UUID;

public record PartitionWorld(Map<String, PartitionDimension> DIMENSIONS, UUID uuid, UUID owner, long seed) {
    public WanderingTraderSpawner getWanderingTraderSpawner() {
        PartitionDimension dimension = DIMENSIONS.get(DimensionConstants.OVERWORLD);
        if (dimension != null) {
            return ((TraderSpawnerAccessor)dimension.dimension().asWorld()).getTraderSpawner();
        } else {
            return null;
        }
    }

    public void unloadDimension(String dimensionId) {
        DIMENSIONS.get(dimensionId).dimension().unload();
    }

    public void loadDimension(String dimensionId) {
        RuntimeWorldConfig worldConfig = CMPRegistry.get(dimensionId);
        worldConfig.setSeed(this.seed);

        this.DIMENSIONS.put(dimensionId, new PartitionDimension(CMP.FANTASY.getOrOpenPersistentWorld(Identifier.of(this.uuid + "_" + dimensionId), worldConfig)));
    }

    public void switchDimension(String oldDimension, String newDimension) {
        unloadDimension(oldDimension);
        loadDimension(newDimension);
    }

    public PartitionWorldData serialize() {
        // TODO Save wandering trader data
        return new PartitionWorldData(this.seed, this.uuid, this.owner, this.DIMENSIONS.keySet());
    }
}
