package com.github.quiltservertools.ticktools;

import com.github.quiltservertools.ticktools.command.TickToolsCommand;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.world.ServerWorld;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.util.HashMap;

public class TickTools implements DedicatedServerModInitializer {
    public static Logger LOGGER;
    private final File configFile = FabricLoader.getInstance().getConfigDir().resolve("ticktools.toml").toFile();

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        ServerWorldEvents.LOAD.register(this::onWorldLoad);
        ServerWorldEvents.UNLOAD.register(this::onWorldUnload);
        ServerPlayConnectionEvents.JOIN.register(this::onPlayerConnect);
        CommandRegistrationCallback.EVENT.register(TickToolsCommand::registerCommands);
    }

    private void onServerStart(MinecraftServer server) {
        LOGGER = LogManager.getLogger();
        var config = TickToolsConfig.loadConfig(configFile);

        // Empty map for world specific distances
        // These are added on world load rather than on server start
        TickToolsManager.setInstance(new TickToolsManager(config, new HashMap<>(), new HashMap<>()));
    }

    private void onWorldLoad(MinecraftServer server, ServerWorld world) {
        var identifier = world.getRegistryKey().getValue();
        var table = TickToolsManager.getInstance().config().toml.getTable(identifier.getPath());

        var config = TickToolsManager.getInstance().config();
        // If table isn't null then we know that it exists
        if (table != null) {
            config = new TickToolsConfig();
            config.readToml(table);
            TickToolsManager.getInstance().worldSpecific().put(identifier, config);
        }

        if (config.dynamic.renderDistance)
            world.getChunkManager().applyViewDistance(config.dynamic.minRenderDistance);
    }

    private void onWorldUnload(MinecraftServer server, ServerWorld world) {
        TickToolsManager.getInstance().worldSpecific().remove(world.getRegistryKey().getValue());
    }

    private void onPlayerConnect(ServerPlayNetworkHandler handler, PacketSender sender, MinecraftServer server) {
        var table = TickToolsManager.getInstance().config().toml.getTable(handler.player.getUuidAsString());

        // If table isn't null then we know that it exists
        if (table != null) {
            var config = new TickToolsConfig();
            config.readToml(table);
            TickToolsManager.getInstance().playerSpecific().put(handler.player.getUuid(), config);
        }
    }
}
