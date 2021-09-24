package com.github.quiltservertools.ticktools.command;

import com.mojang.brigadier.CommandDispatcher;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.util.Formatting;

public class TickToolsCommand {

    public static final Formatting PRIMARY = Formatting.GRAY;
    public static final Formatting SECONDARY = Formatting.BLUE;
    public static final Formatting HEADING = Formatting.AQUA;

    public static void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, boolean dedicated) {
        var node = CommandManager.literal("ticktools").requires(Permissions.require("ticktools.root", 3)).build();
        node.addChild(new StatusCommand().build());
        dispatcher.getRoot().addChild(node);
    }
}
