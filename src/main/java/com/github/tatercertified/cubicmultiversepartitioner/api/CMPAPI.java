package com.github.tatercertified.cubicmultiversepartitioner.api;

import com.github.tatercertified.cubicmultiversepartitioner.partition.PartitionWorld;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;

public interface CMPAPI {
    /**
     * This event is fired when a PartitionWorld is loaded into the game
     */
    interface WorldLoadedEvent {
        Event<WorldLoadedEvent> WORLD_LOAD_EVENT = EventFactory.createArrayBacked(WorldLoadedEvent.class, (listeners) -> (world) -> {
            for (WorldLoadedEvent listener : listeners) {
                ActionResult result = listener.runWorldLoadEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        ActionResult runWorldLoadEvent(PartitionWorld world);
    }

    /**
     * This event is fired when a new PartitionWorld is created
     */
    interface WorldCreatedEvent {
        Event<WorldCreatedEvent> WORLD_CREATED_EVENT = EventFactory.createArrayBacked(WorldCreatedEvent.class, (listeners) -> (world) -> {
            for (WorldCreatedEvent listener : listeners) {
                ActionResult result = listener.runWorldCreatedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        ActionResult runWorldCreatedEvent(PartitionWorld world);
    }

    /**
     * This event is fired when a PartitionWorld is removed
     */
    interface WorldRemovedEvent {
        Event<WorldRemovedEvent> WORLD_REMOVED_EVENT = EventFactory.createArrayBacked(WorldRemovedEvent.class, (listeners) -> (world) -> {
            for (WorldRemovedEvent listener : listeners) {
                ActionResult result = listener.runWorldRemovedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        ActionResult runWorldRemovedEvent(PartitionWorld world);
    }

    /**
     * This event is fired when a PartitionWorld is packed into NBT
     */
    interface WorldSavedEvent {
        Event<WorldSavedEvent> WORLD_SAVED_EVENT = EventFactory.createArrayBacked(WorldSavedEvent.class, (listeners) -> (world) -> {
            for (WorldSavedEvent listener : listeners) {
                ActionResult result = listener.runWorldSavedEvent(world);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        ActionResult runWorldSavedEvent(PartitionWorld world);
    }

    /**
     * This event is fired when a PartitionWorld is ticked (Roughly every 50ms)
     */
    interface WorldTickedEvent {
        Event<WorldTickedEvent> WORLD_TICKED_EVENT = EventFactory.createArrayBacked(WorldTickedEvent.class, (listeners) -> (world, partitionWorld) -> {
            for (WorldTickedEvent listener : listeners) {
                ActionResult result = listener.runWorldTickedEvent(world, partitionWorld);

                if (result != ActionResult.PASS) {
                    return result;
                }
            }
            return ActionResult.PASS;
        });

        ActionResult runWorldTickedEvent(ServerWorld world, PartitionWorld partitionWorld);
    }
}
