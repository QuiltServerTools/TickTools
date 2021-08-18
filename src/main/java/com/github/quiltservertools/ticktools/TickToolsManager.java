package com.github.quiltservertools.ticktools;

import com.github.quiltservertools.ticktools.mixin.MixinThreadedAnvilChunkStorage;
import net.minecraft.entity.ItemEntity;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.Map;
import java.util.UUID;

public record TickToolsManager(TickToolsConfig config, Map<Identifier, TickToolsConfig> worldSpecific, Map<UUID, TickToolsConfig> playerSpecific) {

    private static TickToolsManager instance;

    public static TickToolsManager getInstance() {
        return instance;
    }

    public static void setInstance(TickToolsManager manager) {
        instance = manager;
    }

    public boolean shouldTickChunk(ChunkPos pos, ServerWorld world) {
        // If chunk is force-loaded we return true
        if (world.getForcedChunks().contains(pos.toLong())) return true;
        // First we get the right config, so checking if worldSpecific contains the dimension
        var effectiveConfig = worldSpecific().get(world.getRegistryKey().getValue());
        if (effectiveConfig == null) effectiveConfig = this.config();

        // Ignore tick distance value if split tick distance is disabled
        if (!effectiveConfig.splitTickDistance) return true;
        int tickDistance = getEffectiveTickDistance(world);
        var player = world.getClosestPlayer(pos.getCenterX(), 64, pos.getCenterZ(), world.getHeight() + tickDistance, false);
        if (player != null) {
            if (playerSpecific.containsKey(player.getUuid())) {
                return player.getBlockPos().isWithinDistance(new BlockPos(pos.getCenterX(), player.getY(), pos.getCenterZ()), playerSpecific.get(player.getUuid()).tickDistance);
            } else {
                // The closest player on the server is within the tick distance provided by the config
                return player.getBlockPos().isWithinDistance(new BlockPos(pos.getCenterX(), player.getY(), pos.getCenterZ()), tickDistance);
                // If player is not found within distance then use default return value
            }
        }
        return false;
    }

    public int getItemDespawnTime(ItemEntity item) {
        var player = item.getEntityWorld().getClosestPlayer(item.getX(), item.getY(), item.getZ(), item.getEntityWorld().getHeight(), false);
        if (player != null) {
            if (this.playerSpecific().containsKey(player.getUuid())) {
                return this.playerSpecific().get(player.getUuid()).itemDespawnTicks;
            }
        }
        if (this.worldSpecific().containsKey(item.getEntityWorld().getRegistryKey().getValue())) {
            return this.worldSpecific().get(item.getEntityWorld().getRegistryKey().getValue()).itemDespawnTicks;
        }
        return this.config().itemDespawnTicks;
    }

    public void updateRenderDistance(ServerWorld world) {
        var config = worldSpecific().get(world.getRegistryKey().getValue());
        if (config == null) config = this.config();
        if (config.dynamic.renderDistance) {
            int distance = getEffectiveRenderDistance(world);
            if (((MixinThreadedAnvilChunkStorage) world.getChunkManager().threadedAnvilChunkStorage).getWatchDistance() != distance) {
                world.getChunkManager().applyViewDistance(distance);
                world.getServer().getPlayerManager().sendToAll(new ChunkLoadDistanceS2CPacket(distance));
            }
        }
    }

    public int getEffectiveTickDistance(ServerWorld world) {
        //TODO cache these values
        float time = world.getServer().getTickTime();
        int performanceLevel = getPerformanceLevel(time);

        var config = worldSpecific().get(world.getRegistryKey().getValue());
        if (config == null) config = this.config();

        if (config.dynamic.tickDistance) {
            var distance = config.getTickDistanceBlocks();
            if (performanceLevel == 3) distance = config.dynamic.getMinTickDistanceBlocks();
            else if (performanceLevel == 2)
                distance = Math.min((int)(config.getTickDistanceBlocks() / 1.5F), (int)(config.getTickDistanceBlocks() * 1.5F));
            else if (performanceLevel == 1)
                distance = Math.max((int)(config.getTickDistanceBlocks() / 1.5F), (int)(config.getTickDistanceBlocks() * 1.5F));
            else distance = config.getTickDistanceBlocks();
            return distance;
        }
        return config.tickDistance;
    }

    public int getEffectiveRenderDistance(ServerWorld world) {
        float time = world.getServer().getTickTime();
        int performanceLevel = getPerformanceLevel(time);

        var config = worldSpecific().get(world.getRegistryKey().getValue());
        if (config == null) config = this.config();

        if (config.dynamic.renderDistance) {
            var distance = config.dynamic.maxRenderDistance;
            if (performanceLevel == 3) distance = config.dynamic.minRenderDistance;
            else if (performanceLevel == 2)
                distance = Math.min((int) (config.dynamic.maxRenderDistance / 1.5F), (int) (config.dynamic.minRenderDistance * 1.5F));
            else if (performanceLevel == 1)
                distance = Math.max((int) (config.dynamic.maxRenderDistance / 1.5F), (int) (config.dynamic.minRenderDistance * 1.5F));
            else distance = config.dynamic.minRenderDistance;
            return distance;
        }
        return getWatchDistance(world);
    }

    private int getPerformanceLevel(float time) {
        if (time > 40F) return 3;
        else if (time > 32F) return 2;
        else if (time > 25F) return 1;
        return 1;
    }

    private int getWatchDistance(ServerWorld world) {
        return ((MixinThreadedAnvilChunkStorage) world.getChunkManager().threadedAnvilChunkStorage).getWatchDistance();
    }
}
