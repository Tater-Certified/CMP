package com.github.tatercertified.cubicmultiversepartitioner.mixin;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import com.github.tatercertified.cubicmultiversepartitioner.partition.ServerWorldPartitionInterface;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.block.EndPortalBlock;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EndPortalBlock.class)
public class EndPortalBlockMixin {
    @Redirect(method = "onEntityCollision", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;END:Lnet/minecraft/registry/RegistryKey;"))
    private RegistryKey<World> redirectEnd(@Local(ordinal = 0) World world) {
        if (((ServerWorldPartitionInterface)world).hasParent()) {
            return CMP.manager.getWorld(((ServerWorldPartitionInterface)world).getParentIdentifier()).getEndLikeDimension().getRegistryKey();
        } else {
            return World.END;
        }
    }

    @Redirect(method = "onEntityCollision", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;"))
    private RegistryKey<World> redirectOverworld(@Local(ordinal = 0) World world) {
        if (((ServerWorldPartitionInterface)world).hasParent()) {
            return CMP.manager.getWorld(((ServerWorldPartitionInterface)world).getParentIdentifier()).getOverworldLikeDimension().getRegistryKey();
        } else {
            return World.OVERWORLD;
        }
    }
}
