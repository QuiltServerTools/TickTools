package com.github.quiltservertools.ticktools;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public class TickToolsManager {
    private static TickToolsManager instance;

    private final TickToolsConfig config;

    public TickToolsManager(TickToolsConfig config) {
        this.config = config;
    }

    public static TickToolsManager getInstance() {
        return instance;
    }

    public static void setInstance(TickToolsManager manager) {
        instance = manager;
    }

    public boolean shouldTickChunk(ChunkPos pos, ServerWorld world) {
        if (!config.splitTickDistance()) return true;
        var player = world.getClosestPlayer(pos.getCenterX(), 64, pos.getCenterZ(), world.getHeight() + config.tickDistance(), false);
        if (player != null) {
            if (player.getBlockPos().isWithinDistance(new BlockPos(pos.getCenterX(), player.getY(), pos.getCenterZ()), config.tickDistance())) {
                // The closest player on the server is within the tick distance provided by the config
                return true;
            }
            // If player is not found within distance then use default return value
        }
        return false;
    }
}
