package com.github.quiltservertools.ticktools.mixin;

import com.github.quiltservertools.ticktools.TickToolsManager;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.chunk.WorldChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class MixinServerWorld {
    @Inject(method = "tickEntity", at = @At("HEAD"), cancellable = true)
    public void ticktools$stopEntityTicks(Entity entity, CallbackInfo ci) {
        if (!TickToolsManager.getInstance().shouldTickChunk(entity.getChunkPos(), (ServerWorld) (Object) this)) {
            ci.cancel();
        }
    }

    @Inject(method = "tickChunk", at = @At("HEAD"), cancellable = true)
    public void ticktools$stopChunkTicks(WorldChunk chunk, int randomTickSpeed, CallbackInfo ci) {
        if (!TickToolsManager.getInstance().shouldTickChunk(chunk.getPos(), (ServerWorld) (Object) this)) {
            ci.cancel();
        }
    }
}
