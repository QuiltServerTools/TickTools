package com.github.quiltservertools.ticktools.command.subcommand;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;

public class DistanceSubCommand {
    public static void register(CommandNode<ServerCommandSource> root) {
        var node =  CommandManager.literal("render").then(CommandManager.argument("distance", IntegerArgumentType.integer(2))
                .executes(ctx -> setRenderDistance(ctx, IntegerArgumentType.getInteger(ctx, "distance")))).build();
        root.addChild(node);
        node = CommandManager.literal("tick").then(CommandManager.argument("distance", IntegerArgumentType.integer(2))
                .executes(ctx -> setRenderDistance(ctx, IntegerArgumentType.getInteger(ctx, "distance")))).build();
        root.addChild(node);
    }

    private static int setRenderDistance(CommandContext<ServerCommandSource> context, int distance) {
        context.getSource().getMinecraftServer().getWorlds().forEach(w -> w.getChunkManager().applyViewDistance(distance));
        context.getSource().sendFeedback(new LiteralText("Set render distance to " + distance), false);
        return 1;
    }
}
