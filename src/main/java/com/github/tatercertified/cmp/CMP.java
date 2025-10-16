package com.github.tatercertified.cmp;

import com.github.tatercertified.cmp.api.PartitionEvents;
import com.github.tatercertified.cmp.backend.CMPRegistry;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import xyz.nucleoid.fantasy.Fantasy;

public class CMP implements ModInitializer {
    public static Fantasy FANTASY;

    @Override
    public void onInitialize() {
        ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
            FANTASY = Fantasy.get(minecraftServer);

            CMPRegistry.init(minecraftServer);
            PartitionEvents.PartitionDimensionRegisterEvent.PARTITION_DIMENSION_REGISTER_EVENT.invoker();
        });
    }
}
