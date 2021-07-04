package com.github.quiltservertools.ticktools;

import com.github.quiltservertools.ticktools.mixin.MixinThreadedAnvilChunkStorage;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Map;

public record TickToolsManager(TickToolsConfig config, Map<Identifier, TickToolsConfig> worldSpecific) {

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
            // The closest player on the server is within the tick distance provided by the config
            return player.getBlockPos().isWithinDistance(new BlockPos(pos.getCenterX(), player.getY(), pos.getCenterZ()), tickDistance);
            // If player is not found within distance then use default return value
        }
        return false;
    }

    public void updateRenderDistance(ServerWorld world) {
        if (config.dynamic.renderDistance) {
            int distance = getEffectiveRenderDistance(world.getServer());
            if (((MixinThreadedAnvilChunkStorage) world.getChunkManager().threadedAnvilChunkStorage).getWatchDistance() != distance) {
                world.getChunkManager().applyViewDistance(distance);
                world.getServer().getPlayerManager().sendToAll(new ChunkLoadDistanceS2CPacket(distance));
            }
        }
    }

    private int getEffectiveTickDistance(MinecraftServer server) {
        float time = server.getTickTime();
        int performanceLevel = getPerformanceLevel(time);
        var distance = config.getTickDistanceBlocks();
        if (performanceLevel == 3) distance = config.dynamic.getMinTickDistanceBlocks();
        else if (performanceLevel == 2) distance = Math.min(config.getTickDistanceBlocks() / 2, config.getTickDistanceBlocks() * 2);
        else if (performanceLevel == 1) distance = Math.max(config.getTickDistanceBlocks() / 2, config.getTickDistanceBlocks() * 2);
        else distance = config.getTickDistanceBlocks();
        return distance;
    }

    private int getEffectiveRenderDistance(MinecraftServer server) {
        float time = server.getTickTime();
        int performanceLevel = getPerformanceLevel(time);
        var distance = config().dynamic.maxRenderDistance;
        if (performanceLevel == 3) distance = config.dynamic.minRenderDistance;
        else if (performanceLevel == 2) distance = Math.min(config.dynamic.maxRenderDistance / 2, config.dynamic.minRenderDistance * 2);
        else if (performanceLevel == 1) distance = Math.max(config.dynamic.maxRenderDistance / 2, config.dynamic.minRenderDistance * 2);
        else distance = config.dynamic.minRenderDistance;
        return distance;
    }

    private int getPerformanceLevel(float time) {
        if (time > 40F) return 3;
        else if (time > 32F) return 2;
        else if (time > 25F) return 1;
        return 1;
    }
}
