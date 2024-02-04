package com.github.tatercertified.cubicmultiversepartitioner;

import com.github.tatercertified.cubicmultiversepartitioner.partition.CMPPersistentState;
import com.github.tatercertified.cubicmultiversepartitioner.partition.PartitionManager;
import com.github.tatercertified.cubicmultiversepartitioner.testing.CMPCommand;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CMP implements ModInitializer {

    public static String MODID = "cmp";
    public static PartitionManager manager;
    public static final Logger LOGGER = LoggerFactory.getLogger("Cubic Multiverse Partitioner");


    @Override
    public void onInitialize() {
        CMPCommand.register();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            CMPPersistentState state = CMPPersistentState.getServerState(server);
            manager = new PartitionManager(server, state.partitions);
            state.markDirty();
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> manager.saveAll());
    }

}
