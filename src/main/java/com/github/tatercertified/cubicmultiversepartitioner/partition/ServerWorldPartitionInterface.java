package com.github.tatercertified.cubicmultiversepartitioner.partition;

import net.minecraft.util.Identifier;

public interface ServerWorldPartitionInterface {
    void setParentIdentifier(Identifier identifier);
    Identifier getParentIdentifier();
    boolean hasParent();
}
