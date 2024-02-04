package com.github.tatercertified.cubicmultiversepartitioner.partition;

import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;

public interface ServerWorldPartitionInterface {
    void setParentIdentifier(Identifier identifier);
    Identifier getParentIdentifier();
    boolean hasParent();
    RegistryKey<DimensionType> inheritsDimension();
    void setDimensionInheritance(RegistryKey<DimensionType> type);
}
