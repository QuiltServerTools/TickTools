package com.github.quiltservertools.ticktools;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.world.WorldEvents;
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
    }

    private void onServerStart(MinecraftServer server) {
        LOGGER = LogManager.getLogger();
        var config = TickToolsConfig.loadConfig(configFile);

        // Empty map for world specific distances
        // These are added on world load rather than on server start
        var worlds = new HashMap<Identifier, TickToolsConfig>();

        TickToolsManager.setInstance(new TickToolsManager(config, worlds));
    }

    private void onWorldLoad(MinecraftServer server, ServerWorld world) {
        var identifier = world.getRegistryKey().getValue();
        var table = TickToolsManager.getInstance().config().toml.getTable(identifier.toString());

        // If table isn't null then we know that it exists
        if (table != null) {
            var config = new TickToolsConfig();
            config.readToml(table);
            TickToolsManager.getInstance().worldSpecific().put(identifier, config);
        }
    }
}
