package com.github.quiltservertools.ticktools;

import com.github.quiltservertools.ticktools.mixin.MixinThreadedAnvilChunkStorage;
import com.moandjiezana.toml.Toml;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;

public class TickToolsConfig {

    public boolean splitTickDistance = true;
    public int tickDistance = 2;
    public int itemDespawnTicks = 6000;

    public final TickToolsConfig.Dynamic dynamic = new Dynamic();

    public static class Dynamic {
        public boolean tickDistance;
        public boolean renderDistance;
        public int minTickDistance = 1;
        public int minRenderDistance = 4;
        public int maxRenderDistance = 8;

        public int getMinTickDistanceBlocks() {
            return minTickDistance * 16;
        }

        public int getMinRenderDistanceBlocks() {
            return minRenderDistance * 16;
        }

        public int getMaxRenderDistanceBlocks() {
            return maxRenderDistance * 16;
        }
    }

    public int getTickDistanceBlocks() {
        return tickDistance * 16;
    }

    public static TickToolsConfig loadConfig(File file, MinecraftServer server) {
        TickToolsConfig config = new TickToolsConfig();
        if (file.exists() && file.isFile()) {
            Toml toml = new Toml().read(file);
            config.readToml(toml, server);
        } else {
            TickTools.LOGGER.info("Unable to find config file for TickTools, creating");
            try {
                Files.copy(Objects.requireNonNull(TickToolsConfig.class.getResourceAsStream("/data/ticktools/default_config.toml")), file.toPath());
            } catch (IOException e) {
                TickTools.LOGGER.warn("Unable to create config file for TickTools, using default configuration");
            }
        }

        // Config is default config
        return config;
    }

    private void readToml(Toml toml, MinecraftServer server) {
        this.splitTickDistance = toml.getBoolean("splitTickDistance");
        if (splitTickDistance) {
            this.tickDistance = toml.getLong("tickDistance").intValue();
        }
        Toml dynamicTable = toml.getTable("dynamic");
        this.dynamic.tickDistance = dynamicTable.getBoolean("dynamicTickDistance");
        if (dynamic.tickDistance) {
            dynamic.minTickDistance = dynamicTable.getLong("minTickDistance").intValue();
        }
        this.dynamic.renderDistance = dynamicTable.getBoolean("dynamicRenderDistance");
        if (dynamic.renderDistance) {
            dynamic.minRenderDistance = dynamicTable.getLong("minRenderDistance").intValue();
            dynamic.maxRenderDistance = dynamicTable.getLong("maxRenderDistance").intValue();
        }
        this.itemDespawnTicks = toml.getLong("itemDespawnTicks").intValue();
    }
}


