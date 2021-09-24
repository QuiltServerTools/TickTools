package com.github.quiltservertools.ticktools.command;

import com.github.quiltservertools.ticktools.TickToolsManager;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

import java.util.Objects;

public class StatusCommand implements BuildableCommand {
    @Override
    public LiteralCommandNode<ServerCommandSource> build() {
        return CommandManager.literal("status")
                .executes(ctx -> {
                    prepareMessage(ctx);
                    return 1;
                })
                .build();
    }

    private void prepareMessage(CommandContext<ServerCommandSource> context) {
        var source = context.getSource();
        source.sendFeedback(new LiteralText("--- TickTools Status ---\n").formatted(TickToolsCommand.HEADING), false);
        source.sendFeedback(new LiteralText("Default distances: ").formatted(TickToolsCommand.PRIMARY)
                .append(new LiteralText("" + TickToolsManager.getInstance().getEffectiveRenderDistance(context.getSource().getServer().getOverworld(), false)
                        + "/" + TickToolsManager.getInstance().getEffectiveTickDistance(context.getSource().getServer().getOverworld()) + "\n")
                .formatted(TickToolsCommand.SECONDARY)), false);
        TickToolsManager.getInstance().worldSpecific().forEach(((identifier, tickToolsConfig) -> {
            var key = new LiteralText(identifier.toString() + ":").formatted(TickToolsCommand.PRIMARY);
            var worldKey = Objects.requireNonNull(context.getSource().getServer().getWorld(RegistryKey.of(Registry.WORLD_KEY, identifier)));
            var render = TickToolsManager.getInstance().getEffectiveRenderDistance(worldKey, false);
            var tick = TickToolsManager.getInstance().getEffectiveTickDistance(worldKey);
            var value = new LiteralText("" + render + "/" + tick + "\n").formatted(TickToolsCommand.SECONDARY);
            source.sendFeedback(key.append(value), false);
        }));
    }
}
