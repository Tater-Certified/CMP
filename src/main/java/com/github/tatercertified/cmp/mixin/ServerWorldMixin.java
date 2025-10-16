package com.github.tatercertified.cmp.mixin;

import com.github.tatercertified.cmp.backend.utils.TraderSpawnerAccessor;
import com.github.tatercertified.cmp.backend.utils.WanderingTraderSpawner;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.spawner.SpecialSpawner;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(ServerWorld.class)
public class ServerWorldMixin implements TraderSpawnerAccessor {
    @Shadow
    @Final
    private List<SpecialSpawner> spawners;

    @Override
    public WanderingTraderSpawner getTraderSpawner() {
        return (WanderingTraderSpawner) this.spawners.get(4);
    }
}
