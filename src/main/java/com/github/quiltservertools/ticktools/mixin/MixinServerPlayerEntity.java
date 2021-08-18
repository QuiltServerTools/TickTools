package com.github.quiltservertools.ticktools.mixin;

import com.github.quiltservertools.ticktools.TickToolsManager;
import net.minecraft.network.packet.s2c.play.ChunkLoadDistanceS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class MixinServerPlayerEntity {

    @Inject(method = "worldChanged", at = @At("HEAD"))
    public void ticktools$syncRenderDistance(ServerWorld origin, CallbackInfo ci) {
        ServerWorld world = ((ServerPlayerEntity) (Object) this).getServerWorld();
        int distance = TickToolsManager.getInstance().getEffectiveRenderDistance(world);
        ((ServerPlayerEntity) (Object) this).networkHandler.sendPacket(new ChunkLoadDistanceS2CPacket(distance));
    }
}
