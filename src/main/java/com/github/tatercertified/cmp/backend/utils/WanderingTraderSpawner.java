package com.github.tatercertified.cmp.backend.utils;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnLocation;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.entity.passive.TraderLlamaEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.BiomeTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.poi.PointOfInterestStorage;
import net.minecraft.world.poi.PointOfInterestTypes;
import net.minecraft.world.spawner.SpecialSpawner;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class WanderingTraderSpawner implements SpecialSpawner {
    public static final int DEFAULT_SPAWN_DELAY = 24000;
    private static final int DEFAULT_SPAWN_CHANCE = 25;
    private final Random random = Random.create();
    private int spawnTimer;
    private int spawnDelay;
    private int spawnChance;
    private final NbtCompound data;
    private UUID trader;

    public WanderingTraderSpawner(NbtCompound data) {
        this.data = data;
        this.spawnTimer = 1200;
        this.spawnDelay = data.getInt("delay").orElse(DEFAULT_SPAWN_DELAY);
        this.spawnChance = data.getInt("chance").orElse(DEFAULT_SPAWN_CHANCE);
        if (data.contains("trader")) {
            this.trader = Uuids.toUuid(data.getIntArray("trader").orElse(Uuids.toIntArray(UUID.randomUUID())));
        }

        if (this.spawnDelay == 0 && this.spawnChance == 0) {
            this.spawnDelay = 24000;
            this.spawnChance = 25;
        }
    }

    public void spawn(ServerWorld world, boolean spawnMonsters) {
        if (world.getGameRules().getBoolean(GameRules.DO_TRADER_SPAWNING)) {
            if (--this.spawnTimer <= 0) {
                this.spawnTimer = 1200;
                this.spawnDelay -= 1200;
                if (this.spawnDelay <= 0) {
                    this.spawnDelay = 24000;
                    int i = this.spawnChance;
                    this.spawnChance = MathHelper.clamp(this.spawnChance + 25, 25, 75);
                    if (this.random.nextInt(100) <= i) {
                        if (this.trySpawn(world)) {
                            this.spawnChance = 25;
                        }

                    }
                }
            }
        }
    }

    private boolean trySpawn(ServerWorld world) {
        PlayerEntity playerEntity = world.getRandomAlivePlayer();
        if (playerEntity == null) {
            return true;
        } else if (this.random.nextInt(10) != 0) {
            return false;
        } else {
            BlockPos blockPos = playerEntity.getBlockPos();
            PointOfInterestStorage pointOfInterestStorage = world.getPointOfInterestStorage();
            Optional<BlockPos> optional = pointOfInterestStorage.getPosition((poiType) -> poiType.matchesKey(PointOfInterestTypes.MEETING), (pos) -> true, blockPos, 48, PointOfInterestStorage.OccupationStatus.ANY);
            BlockPos blockPos2 = optional.orElse(blockPos);
            BlockPos blockPos3 = this.getNearbySpawnPos(world, blockPos2, 48);
            if (blockPos3 != null && this.doesNotSuffocateAt(world, blockPos3)) {
                if (world.getBiome(blockPos3).isIn(BiomeTags.WITHOUT_WANDERING_TRADER_SPAWNS)) {
                    return false;
                }

                WanderingTraderEntity wanderingTraderEntity = EntityType.WANDERING_TRADER.spawn(world, blockPos3, SpawnReason.EVENT);
                if (wanderingTraderEntity != null) {
                    for(int j = 0; j < 2; ++j) {
                        this.spawnLlama(world, wanderingTraderEntity);
                    }

                    this.trader = wanderingTraderEntity.getUuid();
                    wanderingTraderEntity.setDespawnDelay(48000);
                    wanderingTraderEntity.setWanderTarget(blockPos2);
                    wanderingTraderEntity.setPositionTarget(blockPos2, 16);
                    return true;
                }
            }

            return false;
        }
    }

    private void spawnLlama(ServerWorld world, WanderingTraderEntity wanderingTrader) {
        BlockPos blockPos = this.getNearbySpawnPos(world, wanderingTrader.getBlockPos(), 4);
        if (blockPos != null) {
            TraderLlamaEntity traderLlamaEntity = EntityType.TRADER_LLAMA.spawn(world, blockPos, SpawnReason.EVENT);
            if (traderLlamaEntity != null) {
                traderLlamaEntity.attachLeash(wanderingTrader, true);
            }
        }
    }

    @Nullable
    private BlockPos getNearbySpawnPos(WorldView world, BlockPos pos, int range) {
        BlockPos blockPos = null;
        SpawnLocation spawnLocation = SpawnRestriction.getLocation(EntityType.WANDERING_TRADER);

        for(int i = 0; i < 10; ++i) {
            int j = pos.getX() + this.random.nextInt(range * 2) - range;
            int k = pos.getZ() + this.random.nextInt(range * 2) - range;
            int l = world.getTopY(Heightmap.Type.WORLD_SURFACE, j, k);
            BlockPos blockPos2 = new BlockPos(j, l, k);
            if (spawnLocation.isSpawnPositionOk(world, blockPos2, EntityType.WANDERING_TRADER)) {
                blockPos = blockPos2;
                break;
            }
        }

        return blockPos;
    }

    private boolean doesNotSuffocateAt(BlockView world, BlockPos pos) {
        for(BlockPos blockPos : BlockPos.iterate(pos, pos.add(1, 2, 1))) {
            if (!world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public NbtCompound toNbt() {
        this.data.putIntArray("trader", Uuids.toIntArray(this.trader));
        this.data.putInt("chance", this.spawnChance);
        this.data.putInt("delay", this.spawnDelay);
        return this.data;
    }
}
