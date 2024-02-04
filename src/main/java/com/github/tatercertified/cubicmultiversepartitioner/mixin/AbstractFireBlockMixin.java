package com.github.tatercertified.cubicmultiversepartitioner.mixin;

import com.github.tatercertified.cubicmultiversepartitioner.partition.ServerWorldPartitionInterface;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(AbstractFireBlock.class)
public class AbstractFireBlockMixin {
    /**
     * @author QPCrummer
     * @reason Redirect to use a Partition's Nether
     */
    @Overwrite
    private static boolean isOverworldOrNether(World world) {
        if (((ServerWorldPartitionInterface)world).hasParent()) {
            return ((ServerWorldPartitionInterface)world).inheritsDimension() == DimensionTypes.OVERWORLD || ((ServerWorldPartitionInterface)world).inheritsDimension() == DimensionTypes.THE_NETHER;
        } else {
            return world.getRegistryKey() == World.OVERWORLD || world.getRegistryKey() == World.NETHER;
        }
    }
}
