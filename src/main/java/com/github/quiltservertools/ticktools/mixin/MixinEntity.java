package com.github.quiltservertools.ticktools.mixin;

import com.github.quiltservertools.ticktools.TickToolsManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {

    @Inject(method = "baseTick", at = @At("HEAD"), cancellable = true)
    public void ticktools$stopEntityTick(CallbackInfo ci) {
        // If chunk should not be ticked we cancel the rest of the method
        if (!TickToolsManager.getInstance().shouldTickChunk(((Entity) (Object) this).getChunkPos(), (ServerWorld) ((Entity) (Object) this).getEntityWorld())) {
            ci.cancel();
        }
    }

}
