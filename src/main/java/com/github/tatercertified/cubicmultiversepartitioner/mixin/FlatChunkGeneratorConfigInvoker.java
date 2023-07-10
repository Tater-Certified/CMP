package com.github.tatercertified.cubicmultiversepartitioner.mixin;

import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSet;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorConfig;
import net.minecraft.world.gen.chunk.FlatChunkGeneratorLayer;
import net.minecraft.world.gen.feature.PlacedFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.List;
import java.util.Optional;

@Mixin(FlatChunkGeneratorConfig.class)
public interface FlatChunkGeneratorConfigInvoker {
    @Invoker("<init>")
    static FlatChunkGeneratorConfig invokeFlatChunkGeneratorConfig(Optional<RegistryEntryList<StructureSet>> structureOverrides, List<FlatChunkGeneratorLayer> layers, boolean lakes, boolean features, Optional<RegistryEntry<Biome>> biome, RegistryEntry.Reference<Biome> fallback, RegistryEntry<PlacedFeature> undergroundLavaLakeFeature, RegistryEntry<PlacedFeature> surfaceLavaLakeFeature) {
        throw new AssertionError();
    }

}
