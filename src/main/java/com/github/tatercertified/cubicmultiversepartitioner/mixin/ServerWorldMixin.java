package com.github.tatercertified.cubicmultiversepartitioner.mixin;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import com.github.tatercertified.cubicmultiversepartitioner.partition.ServerWorldPartitionInterface;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.WorldGenerationProgressListener;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.random.RandomSequencesState;
import net.minecraft.world.dimension.DimensionOptions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.level.ServerWorldProperties;
import net.minecraft.world.level.storage.LevelStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.concurrent.Executor;

@Mixin(ServerWorld.class)
public abstract class ServerWorldMixin implements ServerWorldPartitionInterface {

    @Shadow private @Nullable EnderDragonFight enderDragonFight;

    @Shadow public abstract @NotNull MinecraftServer getServer();

    private Identifier parentPartitionWorld;
    private RegistryKey<DimensionType> inheritance;

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

    @Override
    public RegistryKey<DimensionType> inheritsDimension() {
        return inheritance;
    }

    @Override
    public void setDimensionInheritance(RegistryKey<DimensionType> type) {
        this.inheritance = type;
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void setDragonFight(MinecraftServer server, Executor workerExecutor, LevelStorage.Session session, ServerWorldProperties properties, RegistryKey worldKey, DimensionOptions dimensionOptions, WorldGenerationProgressListener worldGenerationProgressListener, boolean debugWorld, long seed, List spawners, boolean shouldTickTime, RandomSequencesState randomSequencesState, CallbackInfo ci) {
        if (((ServerWorld)(Object)this).getDimensionEntry().matchesKey(DimensionTypes.THE_END)) {
            if (((ServerWorldPartitionInterface)this).hasParent()) {
                // TODO Determine if I should add the EnderDragonFight in this
                // this.enderDragonFight = new EnderDragonFight(((ServerWorld) (Object) this), CMP.manager.getWorld(((ServerWorldPartitionInterface)this).getParentIdentifier()).seed(), island.getDragonFight());
            } else {
                this.enderDragonFight = new EnderDragonFight(((ServerWorld) (Object) this), this.getServer().getSaveProperties().getGeneratorOptions().getSeed(), this.getServer().getSaveProperties().getDragonFight());
            }
        }
    }
}
