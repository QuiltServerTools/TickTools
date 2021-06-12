package com.github.quiltservertools.ticktools;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

public record TickToolsManager(TickToolsConfig config) {
    private static TickToolsManager instance;

    public static TickToolsManager getInstance() {
        return instance;
    }

    public static void setInstance(TickToolsManager manager) {
        instance = manager;
    }

    public boolean shouldTickChunk(ChunkPos pos, ServerWorld world) {
        // Ignore tick distance value if split tick distance is disabled
        if (!config.splitTickDistance) return true;
        int tickDistance = config().getTickDistanceBlocks();
        // Now we call the dynamic tick distance check
        if (config.dynamic.tickDistance) tickDistance = getEffectiveTickDistance(world.getServer());
        var player = world.getClosestPlayer(pos.getCenterX(), 64, pos.getCenterZ(), world.getHeight() + tickDistance, false);
        if (player != null) {
            if (player.getBlockPos().isWithinDistance(new BlockPos(pos.getCenterX(), player.getY(), pos.getCenterZ()), tickDistance)) {
                // The closest player on the server is within the tick distance provided by the config
                return true;
            }
            // If player is not found within distance then use default return value
        }
        return false;
    }

    private int getEffectiveTickDistance(MinecraftServer server) {
        float time = server.getTickTime();
        var distance = config.getTickDistanceBlocks();
        if (time > 40F) distance = config.dynamic.getMinTickDistanceBlocks();
        else if (time > 32F) distance = Math.min(config.getTickDistanceBlocks() / 2, config.getTickDistanceBlocks() * 2);
        else if (time > 25F) distance = Math.max(config.getTickDistanceBlocks() / 2, config.getTickDistanceBlocks() * 2);
        else distance = config.getTickDistanceBlocks();
        return distance;
    }
}
