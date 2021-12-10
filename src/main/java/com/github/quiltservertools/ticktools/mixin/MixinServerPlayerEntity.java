package com.github.quiltservertools.ticktools.mixin;


import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;
    
    @Inject(method = "worldChanged", at = @At("HEAD"))
    public void ticktools$syncRenderDistance(ServerWorld origin, CallbackInfo ci) {
        ServerWorld world = this.getWorld();
        int distance = ((MixinThreadedAnvilChunkStorage) world.getChunkManager().threadedAnvilChunkStorage).getWatchDistance();
        networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(distance));
    }
    
    @Shadow
    public abstract ServerWorld getWorld();
}
