package com.github.quiltservertools.ticktools;

import com.github.quiltservertools.ticktools.command.Commands;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;

public class TickTools implements DedicatedServerModInitializer {
    public static Logger LOGGER;
    private final File configFile = FabricLoader.getInstance().getConfigDir().resolve("ticktools.json").toFile();

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStart);
        ServerLifecycleEvents.SERVER_STOPPING.register(this::onServerStop);
        CommandRegistrationCallback.EVENT.register(Commands::register);
    }

    private void onServerStart(MinecraftServer server) {
        LOGGER = LogManager.getLogger();
        var config = TickToolsConfig.loadConfig(configFile);
        TickToolsManager.setInstance(new TickToolsManager(config));
    }

    private void onServerStop(MinecraftServer server) {
        TickToolsManager.getInstance().config().saveConfig(configFile);
    }
}
