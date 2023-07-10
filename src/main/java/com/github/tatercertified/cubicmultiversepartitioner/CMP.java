package com.github.tatercertified.cubicmultiversepartitioner;

import com.github.tatercertified.cubicmultiversepartitioner.partition.CMPPersistentState;
import com.github.tatercertified.cubicmultiversepartitioner.partition.PartitionManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class CMP implements ModInitializer {

    public static String MODID = "cmp";
    private final PartitionManager manager = new PartitionManager();


    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CMPPersistentState state = CMPPersistentState.getServerState(server);
            manager.globalCounter = state.counter;
        });
    }
}
