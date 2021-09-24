package com.github.quiltservertools.ticktools.command;

import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.ServerCommandSource;

public interface BuildableCommand {
    LiteralCommandNode<ServerCommandSource> build();
}
