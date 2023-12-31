package com.github.tatercertified.cubicmultiversepartitioner.partition;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class CMPPersistentState extends PersistentState {
    public NbtList partitions = new NbtList();
    public long counter = 0;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("partitions", partitions);
        nbt.put("counter", NbtLong.of(counter));
        return nbt;
    }

    public static CMPPersistentState createFromNbt(NbtCompound nbt) {
        CMPPersistentState state = new CMPPersistentState();
        state.partitions = nbt.getList("partitions", NbtElement.COMPOUND_TYPE);
        state.counter = nbt.getLong("counter");
        return state;
    }


    public static CMPPersistentState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        return persistentStateManager.getOrCreate(
                CMPPersistentState::createFromNbt,
                CMPPersistentState::new,
                CMP.MODID);
    }
}
