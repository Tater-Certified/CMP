package com.github.tatercertified.cubicmultiversepartitioner.mixin;

import com.github.tatercertified.cubicmultiversepartitioner.partition.ServerWorldPartitionInterface;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements ServerWorldPartitionInterface {

    private Identifier parentPartitionWorld;

    @Override
    public void setParentIdentifier(Identifier identifier) {
        this.parentPartitionWorld = identifier;
    }

    @Override
    public Identifier getParentIdentifier() {
        return this.parentPartitionWorld;
    }

    @Override
    public boolean hasParent() {
        return parentPartitionWorld != null;
    }
}
