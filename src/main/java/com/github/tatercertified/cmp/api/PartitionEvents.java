package com.github.tatercertified.cmp.api;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public interface PartitionEvents {
    interface PartitionCreatedEvent {
        Event<PartitionCreatedEvent> PARTITION_CREATED_EVENT = EventFactory.createArrayBacked(PartitionCreatedEvent.class, (listeners) -> (world) -> {
            for (PartitionCreatedEvent listener : listeners) {
                ActionResult result = listener.runPartitionCreatedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to Partition object
        ActionResult runPartitionCreatedEvent(ServerWorld world);
    }

    interface PartitionRemovedEvent {
        Event<PartitionRemovedEvent> PARTITION_REMOVED_EVENT = EventFactory.createArrayBacked(PartitionRemovedEvent.class, (listeners) -> (world) -> {
            for (PartitionRemovedEvent listener : listeners) {
                ActionResult result = listener.runPartitionRemovedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to Partition object
        ActionResult runPartitionRemovedEvent(ServerWorld world);
    }

    interface PartitionSavedEvent {
        Event<PartitionSavedEvent> PARTITION_SAVED_EVENT = EventFactory.createArrayBacked(PartitionSavedEvent.class, (listeners) -> (world) -> {
            for (PartitionSavedEvent listener : listeners) {
                ActionResult result = listener.runPartitionSavedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to Partition object
        ActionResult runPartitionSavedEvent(ServerWorld world);
    }

    interface PartitionWorldLoadedEvent {
        Event<PartitionWorldLoadedEvent> WORLD_LOADED_EVENT = EventFactory.createArrayBacked(PartitionWorldLoadedEvent.class, (listeners) -> (world) -> {
            for (PartitionWorldLoadedEvent listener : listeners) {
                ActionResult result = listener.runPartitionWorldLoadedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to PartitionWorld object
        ActionResult runPartitionWorldLoadedEvent(ServerWorld world);
    }

    interface PartitionWorldUnloadedEvent {
        Event<PartitionWorldUnloadedEvent> WORLD_UNLOADED_EVENT = EventFactory.createArrayBacked(PartitionWorldUnloadedEvent.class, (listeners) -> (world) -> {
            for (PartitionWorldUnloadedEvent listener : listeners) {
                ActionResult result = listener.runPartitionWorldUnloadedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to PartitionWorld object
        ActionResult runPartitionWorldUnloadedEvent(ServerWorld world);
    }

    interface PlayerJoinEvent {
        Event<PlayerJoinEvent> PLAYER_JOIN_EVENT = EventFactory.createArrayBacked(PlayerJoinEvent.class, (listeners) -> (player, world) -> {
            for (PlayerJoinEvent listener : listeners) {
                ActionResult result = listener.runPlayerJoinEvent(player, world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to PartitionWorld object
        ActionResult runPlayerJoinEvent(ServerPlayerEntity player, ServerWorld world);
    }

    interface PlayerLeaveEvent {
        Event<PlayerLeaveEvent> PLAYER_LEAVE_EVENT = EventFactory.createArrayBacked(PlayerLeaveEvent.class, (listeners) -> (player, world) -> {
            for (PlayerLeaveEvent listener : listeners) {
                ActionResult result = listener.runPlayerLeaveEvent(player, world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to PartitionWorld object
        ActionResult runPlayerLeaveEvent(ServerPlayerEntity player, ServerWorld world);
    }

    interface PartitionWorldTickEvent {
        Event<PartitionWorldTickEvent> WORLD_TICK_EVENT = EventFactory.createArrayBacked(PartitionWorldTickEvent.class, (listeners) -> (world) -> {
            for (PartitionWorldTickEvent listener : listeners) {
                ActionResult result = listener.runPartitionWorldTickEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        // TODO Change this to PartitionWorld object
        ActionResult runPartitionWorldTickEvent(ServerWorld world);
    }
}
