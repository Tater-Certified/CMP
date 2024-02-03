package com.github.tatercertified.cubicmultiversepartitioner;

import com.github.tatercertified.cubicmultiversepartitioner.partition.CMPPersistentState;
import com.github.tatercertified.cubicmultiversepartitioner.partition.PartitionManager;
import com.github.tatercertified.cubicmultiversepartitioner.testing.CMPCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class CMP implements ModInitializer {

    public static String MODID = "cmp";
    public static PartitionManager manager;


    @Override
    public void onInitialize() {
        CMPCommand.register();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> manager = new PartitionManager(server, CMPPersistentState.getServerState(server).partitions));
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> manager.saveAll());
    }

}
