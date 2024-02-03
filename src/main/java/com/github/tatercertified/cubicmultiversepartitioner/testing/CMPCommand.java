package com.github.tatercertified.cubicmultiversepartitioner.testing;

import com.github.tatercertified.cubicmultiversepartitioner.CMP;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.IdentifierArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;

import java.util.concurrent.ThreadLocalRandom;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class CMPCommand {
    public static void register() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated, environment) -> dispatcher.register(CommandManager.literal("cmp")
                .executes(context -> {
                    context.getSource().sendFeedback(() -> Text.of("There are " + CMP.manager.getWorldCount() + " partitions loaded"), true);
                    return 1;
                })

                .then(literal("create").then(argument("identifier", IdentifierArgumentType.identifier()).executes(context -> {
                    CMP.manager.createWorld(IdentifierArgumentType.getIdentifier(context, "identifier"), ThreadLocalRandom.current().nextLong(), 400, new Identifier[]{new Identifier("minecraft:overworld")}, true);
                    return 1;
                })))

                .then(literal("load").then(argument("identifier", IdentifierArgumentType.identifier()).executes(context -> {
                    CMP.manager.loadWorld(IdentifierArgumentType.getIdentifier(context, "identifier"));
                    return 1;
                })))

                .then(literal("teleport").then(argument("identifier", IdentifierArgumentType.identifier()).executes(context -> {
                    CMP.manager.teleportToWorld(IdentifierArgumentType.getIdentifier(context, "identifier"), null, context.getSource().getEntity(), Vec3d.ZERO);
                    return 1;
                })))

                .then(literal("remove").then(argument("identifier", IdentifierArgumentType.identifier()).executes(context -> {
                    CMP.manager.removeWorld(IdentifierArgumentType.getIdentifier(context, "identifier"));
                    return 1;
                })))
        ));
    }
}
