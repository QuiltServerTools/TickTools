package com.github.quiltservertools.ticktools;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.server.MinecraftServer;

public class TickTools implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
    }

    private void onServerStart(MinecraftServer server) {
        // Multiply chunks by 16 to get blocks
        //TODO change this to a config file
        var config = new TickToolsConfig(true, 2 * 16, false);
        TickToolsManager.setInstance(new TickToolsManager(config));
    }
}
