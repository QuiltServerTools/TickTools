package com.github.quiltservertools.ticktools.mixin;

import com.github.quiltservertools.ticktools.TickToolsManager;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    public void ticktools$stopLivingEntityTick(CallbackInfo ci) {
        // If chunk should not be ticked we cancel the rest of the method
        if (!TickToolsManager.getInstance().shouldTickChunk(((LivingEntity) (Object) this).getChunkPos(), (ServerWorld) ((LivingEntity) (Object) this).getEntityWorld())) {
            ci.cancel();
        }
    }
}
