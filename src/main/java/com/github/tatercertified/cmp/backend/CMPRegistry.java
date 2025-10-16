package com.github.tatercertified.cmp.backend;

import com.github.tatercertified.cmp.backend.utils.WanderingTraderSpawner;
import com.github.tatercertified.cmp.mixin.RuntimeWorldAccessor;
import com.google.common.collect.ImmutableList;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Util;
import net.minecraft.village.ZombieSiegeManager;
import net.minecraft.world.World;
import net.minecraft.world.biome.source.BiomeAccess;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.spawner.CatSpawner;
import net.minecraft.world.spawner.PatrolSpawner;
import net.minecraft.world.spawner.PhantomSpawner;
import xyz.nucleoid.fantasy.RuntimeWorldConfig;
import xyz.nucleoid.fantasy.RuntimeWorldProperties;
import xyz.nucleoid.fantasy.mixin.MinecraftServerAccess;

import java.util.HashMap;
import java.util.Map;

public final class CMPRegistry {
    private static final Map<String, RuntimeWorldConfig> DIMENSION_CONFIGS = new HashMap<>();

    // Add default configs
    public static void init(MinecraftServer server) {
        // Overworld
        RuntimeWorldConfig overworldConfig = new RuntimeWorldConfig()
                .setDimensionType(DimensionTypes.OVERWORLD)
                .setDifficulty(server.getOverworld().getDifficulty())
                .setShouldTickTime(true)
                .setSunny(-1)
                .setWorldConstructor((server1, registryKey, config, style) -> RuntimeWorldAccessor.init(
                        server1,
                        Util.getMainWorkerExecutor(),
                        ((MinecraftServerAccess) server1).getSession(),
                        new RuntimeWorldProperties(server1.getSaveProperties(), config),
                        registryKey,
                        config.createDimensionOptions(server1),
                        false,
                        BiomeAccess.hashSeed(config.getSeed()),
                        ImmutableList.of(new PhantomSpawner(), new PatrolSpawner(), new CatSpawner(), new ZombieSiegeManager(), new WanderingTraderSpawner(new NbtCompound())),
                        config.shouldTickTime(),
                        null, style
                ))
                .setGenerator(server.getOverworld().getChunkManager().getChunkGenerator());
        register(DimensionConstants.OVERWORLD, overworldConfig);

        // Nether
        RuntimeWorldConfig netherConfig = new RuntimeWorldConfig()
                .setDimensionType(DimensionTypes.THE_NETHER)
                .setDifficulty(server.getOverworld().getDifficulty())
                .setShouldTickTime(true)
                .setWorldConstructor((server1, registryKey, config, style) -> RuntimeWorldAccessor.init(
                        server1,
                        Util.getMainWorkerExecutor(),
                        ((MinecraftServerAccess) server1).getSession(),
                        new RuntimeWorldProperties(server1.getSaveProperties(), config),
                        registryKey,
                        config.createDimensionOptions(server1),
                        false,
                        BiomeAccess.hashSeed(config.getSeed()),
                        ImmutableList.of(new ZombieSiegeManager()),
                        config.shouldTickTime(),
                        null, style))
                .setGenerator(server.getWorld(World.NETHER).getChunkManager().getChunkGenerator());
        register(DimensionConstants.NETHER, netherConfig);

        // End
        RuntimeWorldConfig endConfig = new RuntimeWorldConfig()
                .setDimensionType(DimensionTypes.THE_END)
                .setDifficulty(server.getOverworld().getDifficulty())
                .setShouldTickTime(true)
                .setWorldConstructor((server1, registryKey, config, style) -> RuntimeWorldAccessor.init(
                        server1,
                        Util.getMainWorkerExecutor(),
                        ((MinecraftServerAccess) server1).getSession(),
                        new RuntimeWorldProperties(server1.getSaveProperties(), config),
                        registryKey,
                        config.createDimensionOptions(server1),
                        false,
                        BiomeAccess.hashSeed(config.getSeed()),
                        ImmutableList.of(new ZombieSiegeManager()),
                        config.shouldTickTime(),
                        null, style))
                .setGenerator(server.getWorld(World.END).getChunkManager().getChunkGenerator());
        register(DimensionConstants.END, endConfig);
    }

    public static void register(String dimensionId, RuntimeWorldConfig config) {
        DIMENSION_CONFIGS.put(dimensionId, config);
    }

    public static RuntimeWorldConfig get(String dimensionId) {
        return DIMENSION_CONFIGS.get(dimensionId);
    }
}
