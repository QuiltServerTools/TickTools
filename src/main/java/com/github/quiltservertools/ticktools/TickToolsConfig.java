package com.github.quiltservertools.ticktools;

import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

public class TickToolsConfig {
    private boolean splitTickDistance;
    private boolean dynamicTickDistance;
    private boolean dynamicRenderDistance;

    private int tickDistance;
    private int minTickDistance;
    private int minRenderDistance;

    private TickToolsConfig(boolean splitTickDistance, int tickDistance, boolean dynamicTickDistance, int minTickDistance, boolean dynamicRenderDistance, int minRenderDistance) {
        this.splitTickDistance = splitTickDistance;
        this.dynamicTickDistance = dynamicTickDistance;
        this.dynamicRenderDistance = dynamicRenderDistance;
        // All params are measured in chunks
        // But we want them to be measured in blocks, so we multiply by 16
        this.tickDistance = tickDistance * 16;
        this.minTickDistance = minTickDistance * 16;
        this.minRenderDistance = minRenderDistance * 16;
    }

    public boolean isSplitTickDistance() {
        return splitTickDistance;
    }

    public boolean isDynamicTickDistance() {
        return dynamicTickDistance;
    }

    public boolean isDynamicRenderDistance() {
        return dynamicRenderDistance;
    }

    public int getTickDistance() {
        return tickDistance;
    }

    public int getMinTickDistance() {
        return minTickDistance;
    }

    public int getMinRenderDistance() {
        return minRenderDistance;
    }

    public void setSplitTickDistance(boolean splitTickDistance) {
        this.splitTickDistance = splitTickDistance;
    }

    public void setDynamicTickDistance(boolean dynamicTickDistance) {
        this.dynamicTickDistance = dynamicTickDistance;
    }

    public void setDynamicRenderDistance(boolean dynamicRenderDistance) {
        this.dynamicRenderDistance = dynamicRenderDistance;
    }

    public void setTickDistance(int tickDistance) {
        this.tickDistance = tickDistance;
    }

    public void setMinTickDistance(int minTickDistance) {
        this.minTickDistance = minTickDistance;
    }

    public void setMinRenderDistance(int minRenderDistance) {
        this.minRenderDistance = minRenderDistance;
    }

    public static TickToolsConfig parse(Path path) {
        try {
            var json = new JsonParser().parse(Files.readString(path)).getAsJsonObject();
            var splitTickDistance = json.get("split_tick_distance").getAsBoolean();
            var tickDistance = json.get("tick_distance").getAsInt();
            var dynamic = json.get("dynamic").getAsJsonObject();
            var isDynamicTick = dynamic.get("dynamic_tick_distance").getAsBoolean();
            var isDynamicRender = dynamic.get("dynamic_render_distance").getAsBoolean();
            var minTickDistance = dynamic.get("min_tick_distance").getAsInt();
            var minRenderDistance = dynamic.get("min_render_distance").getAsInt();
            return new TickToolsConfig(splitTickDistance, tickDistance, isDynamicTick, minTickDistance, isDynamicRender, minRenderDistance);
        } catch (IOException e) {
            TickTools.LOGGER.info("Unable to find config file for TickTools, creating");
            try {
                Files.copy(Objects.requireNonNull(TickToolsConfig.class.getResourceAsStream("/data/ticktools/default_config.json")), path);
            } catch (IOException ioException) {
                TickTools.LOGGER.error("Unable to copy TickTools default config file");
            }
            return new TickToolsConfig(false, 0, false, 0, false, 0);
        }
    }
}


