package com.github.quiltservertools.ticktools;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TickTools implements DedicatedServerModInitializer {
    public static Logger LOGGER;

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
    }

    private void onServerStart(MinecraftServer server) {
        LOGGER = LogManager.getLogger();
        var config = TickToolsConfig.parse(FabricLoader.getInstance().getConfigDir().resolve("ticktools.json"));
        TickToolsManager.setInstance(new TickToolsManager(config));
    }
}
