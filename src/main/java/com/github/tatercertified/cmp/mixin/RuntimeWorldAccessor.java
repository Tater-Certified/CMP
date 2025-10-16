package com.github.tatercertified.cmp.mixin;

import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import net.minecraft.world.spawner.SpecialSpawner;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import xyz.nucleoid.fantasy.RuntimeWorld;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(value = RuntimeWorld.class, remap = false)
public interface RuntimeWorldAccessor {
    @Invoker("<init>")
    static RuntimeWorld init(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey<World> worldKey, DimensionOptions dimensionOptions, boolean debugWorld, long seed, List<SpecialSpawner> spawners, boolean shouldTickTime, @Nullable RandomSequencesState randomSequencesState, RuntimeWorld.Style style) {
        throw new AssertionError();
    }
}
