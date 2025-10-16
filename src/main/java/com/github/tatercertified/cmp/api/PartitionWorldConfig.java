package com.github.tatercertified.cmp.api;

import net.minecraft.util.Pair;
import net.minecraft.world.GameRules;

import java.util.Set;

public record PartitionWorldConfig(Set<String> dimensions, Set<Pair<GameRules.Key<GameRules.BooleanRule>, Boolean>> defaultBoolGameRules, Set<Pair<GameRules.Key<GameRules.IntRule>, Integer>> defaultIntGameRules) {

}
