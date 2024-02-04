package com.github.tatercertified.cubicmultiversepartitioner.partition;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import net.minecraft.datafixer.DataFixTypes;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

public class CMPPersistentState extends PersistentState {
    public NbtList partitions = new NbtList();
    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.put("partitions", partitions);
        return nbt;
    }

    private static CMPPersistentState createFromNbt(NbtCompound nbt) {
        CMPPersistentState state = new CMPPersistentState();
        state.partitions = nbt.getList("partitions", NbtElement.COMPOUND_TYPE);
        return state;
    }

    private static PersistentState.Type<CMPPersistentState> getPersistentStateType() {
        return new PersistentState.Type<>(CMPPersistentState::new, CMPPersistentState::createFromNbt, DataFixTypes.LEVEL);
    }


    public static CMPPersistentState getServerState(MinecraftServer server) {
        PersistentStateManager persistentStateManager = server.getWorld(World.OVERWORLD).getPersistentStateManager();

        return persistentStateManager.getOrCreate(
                CMPPersistentState.getPersistentStateType(),
                CMP.MODID);
    }
}
