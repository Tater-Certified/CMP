package com.github.tatercertified.cubicmultiversepartitioner.mixin;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import com.github.tatercertified.cubicmultiversepartitioner.partition.ServerWorldPartitionInterface;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Redirect(method = "tickWorlds", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void redirectTickWorld(ServerWorld instance, BooleanSupplier shouldKeepTicking) {
        if (((ServerWorldPartitionInterface)instance).hasParent()) {
            CMP.manager.getPartitionWorld(instance).tick();
        } else {
            instance.tick(shouldKeepTicking);
        }
    }
}
