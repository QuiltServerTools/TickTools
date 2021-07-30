package com.github.quiltservertools.ticktools;

import com.moandjiezana.toml.Toml;
import net.minecraft.util.Identifier;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TickToolsConfig {

    public boolean splitTickDistance = true;
    public int tickDistance = 2;
    public int itemDespawnTicks = 6000;
    public Map<Identifier, Integer> customItemValues = new HashMap<>();

    public TickToolsConfig.Dynamic dynamic = new Dynamic();
    public Toml toml;

    public static class Dynamic {
        public boolean tickDistance;
        public boolean renderDistance;
        public int minTickDistance = 4;
        public int minRenderDistance = 8;
        public int maxRenderDistance = 12;

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

    public static TickToolsConfig loadConfig(File file) {
        TickToolsConfig config = new TickToolsConfig();
        if (file.exists() && file.isFile()) {
            Toml toml = new Toml().read(file);
            config.readToml(toml, null);
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

    protected void readToml(Toml toml, String path) {

        /*
         Required config options
         These must be present
         */

        this.splitTickDistance = toml.getBoolean("splitTickDistance");

        /*
        Parsing of additional values
         */

        if (splitTickDistance) {
            this.tickDistance = toml.getLong("tickDistance").intValue();
        }

        /*
        Optional config options
        Must contain default value
         */

        if (path != null && toml.containsTable(path + "_dynamic")) {
            Toml dynamicTable = toml.getTable(path + "_dynamic");
            readDynamicTable(dynamicTable);
        } else if (toml.containsTable("dynamic")) {
            Toml dynamicTable = toml.getTable("dynamic");
            readDynamicTable(dynamicTable);
        }

        if (toml.contains("itemDespawnTicks")) {
            this.itemDespawnTicks = toml.getLong("itemDespawnTicks").intValue();
        }

        this.toml = toml;
    }

    private void readDynamicTable(Toml dynamicTable) {
        this.dynamic.tickDistance = dynamicTable.getBoolean("dynamicTickDistance");
        if (dynamic.tickDistance) {
            dynamic.minTickDistance = dynamicTable.getLong("minTickDistance").intValue();
        }
        this.dynamic.renderDistance = dynamicTable.getBoolean("dynamicRenderDistance");
        if (dynamic.renderDistance) {
            dynamic.minRenderDistance = dynamicTable.getLong("minRenderDistance").intValue();
            dynamic.maxRenderDistance = dynamicTable.getLong("maxRenderDistance").intValue();
        }
    }
}


