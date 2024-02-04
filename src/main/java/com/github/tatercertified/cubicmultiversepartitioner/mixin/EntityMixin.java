package com.github.tatercertified.cubicmultiversepartitioner.mixin;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import com.github.tatercertified.cubicmultiversepartitioner.partition.ServerWorldPartitionInterface;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Entity.class)
public class EntityMixin {
    @Shadow private World world;

    @Redirect(method = "tickPortal", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;NETHER:Lnet/minecraft/registry/RegistryKey;", opcode = Opcodes.GETSTATIC))
    private RegistryKey<World> redirectNetherRegistryKey() {
        if (((ServerWorldPartitionInterface)world).hasParent()) {
            return CMP.manager.getWorld(((ServerWorldPartitionInterface)world).getParentIdentifier()).getNetherLikeDimension().getRegistryKey();
        } else {
            return World.NETHER;
        }
    }

    @Redirect(method = "tickPortal", at = @At(value = "FIELD", target = "Lnet/minecraft/world/World;OVERWORLD:Lnet/minecraft/registry/RegistryKey;", opcode = Opcodes.GETSTATIC))
    private RegistryKey<World> redirectOverworldRegistryKey() {
        if (((ServerWorldPartitionInterface)world).hasParent()) {
            return CMP.manager.getWorld(((ServerWorldPartitionInterface)world).getParentIdentifier()).getOverworldLikeDimension().getRegistryKey();
        } else {
            return World.OVERWORLD;
        }
    }
}
