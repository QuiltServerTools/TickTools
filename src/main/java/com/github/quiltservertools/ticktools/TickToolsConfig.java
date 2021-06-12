package com.github.quiltservertools.ticktools;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class TickToolsConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public boolean splitTickDistance = true;
    public int tickDistance = 2;

    public final TickToolsConfig.Dynamic dynamic = new Dynamic();

    public static class Dynamic {
        public boolean tickDistance;
        public boolean renderDistance;
        public int minTickDistance = 1;
        public int minRenderDistance = 4;

        public int getMinTickDistanceBlocks() {
            return minTickDistance * 16;
        }

        public int getMinRenderDistanceBlocks() {
            return minRenderDistance * 16;
        }
    }

    public int getTickDistanceBlocks() {
        return tickDistance * 16;
    }

    public static TickToolsConfig loadConfig(File file) {
        TickToolsConfig config = new TickToolsConfig();

        if (file.exists() && file.isFile()) {
            try (
                    FileInputStream fileInputStream = new FileInputStream(file);
                    InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader)
            ) {
                config = GSON.fromJson(bufferedReader, TickToolsConfig.class);
            } catch (IOException e) {
                TickTools.LOGGER.error("Failed to load config", e);
            }
        } else {
            TickTools.LOGGER.info("Unable to find config file for TickTools, creating");
        }

        config.saveConfig(file);

        return config;
    }

    public void saveConfig(File config) {
        try (
                FileOutputStream stream = new FileOutputStream(config);
                Writer writer = new OutputStreamWriter(stream, StandardCharsets.UTF_8)
        ) {
            GSON.toJson(this, writer);
        } catch (IOException e) {
            TickTools.LOGGER.error("Failed to save config", e);
        }
    }
}


