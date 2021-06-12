package com.github.quiltservertools.ticktools.command.subcommand;

import com.github.quiltservertools.ticktools.TickToolsManager;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class DynamicSubCommand {
    public static void register(CommandNode<ServerCommandSource> root) {
        final var distanceArg = CommandManager.argument("distance", IntegerArgumentType.integer(2));
        var node = CommandManager.literal("dynamic")
                .then(CommandManager.literal("tick").then(distanceArg).executes(ctx ->
                        setDynamicInfo(true, IntegerArgumentType.getInteger(ctx, "distance"),
                                TickToolsManager.getInstance().config().dynamic.renderDistance, TickToolsManager.getInstance().config().dynamic.minRenderDistance))
                        .then(CommandManager.literal("disable").executes(ctx ->
                                setDynamicInfo(false, 0,
                                        TickToolsManager.getInstance().config().dynamic.renderDistance, TickToolsManager.getInstance().config().dynamic.minRenderDistance)))
                        .then(CommandManager.literal("view")).then(distanceArg).executes(ctx ->
                                setDynamicInfo(TickToolsManager.getInstance().config().dynamic.tickDistance, TickToolsManager.getInstance().config().dynamic.minTickDistance,
                                        true, IntegerArgumentType.getInteger(ctx, "distance")))
                        .then(CommandManager.literal("disable").executes(ctx ->
                                setDynamicInfo(TickToolsManager.getInstance().config().dynamic.tickDistance, TickToolsManager.getInstance().config().dynamic.minTickDistance,
                                        false, TickToolsManager.getInstance().config().dynamic.minRenderDistance))))
                .build();
        root.addChild(node);
    }

    private static int setDynamicInfo(boolean tickDistanceDynamic, int tickDistanceValue, boolean renderDistanceDynamic, int renderDistanceValue) {
        var config = TickToolsManager.getInstance().config();
        if (tickDistanceDynamic) {
            config.dynamic.tickDistance = true;
            config.dynamic.minTickDistance = tickDistanceValue;
        }
        if (renderDistanceDynamic) {
            config.dynamic.renderDistance = true;
            config.dynamic.minRenderDistance = renderDistanceValue;
        }
        return 1;
    }
}
