package com.github.quiltservertools.ticktools.command;

import com.github.quiltservertools.ticktools.command.subcommand.DistanceSubCommand;
import com.github.quiltservertools.ticktools.command.subcommand.DynamicSubCommand;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

public class Commands {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        var node = dispatcher.register(CommandManager.literal("ticktools"));
        dispatcher.getRoot().addChild(node);
        DynamicSubCommand.register(node);
        DistanceSubCommand.register(node);
    }
}
