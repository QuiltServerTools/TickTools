package com.github.quiltservertools.ticktools.mixin;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public class MixinEntity {
    @Inject(method = "baseTick", at = @At("HEAD"), cancellable = true)
    public void ticktools$stopEntityTick(CallbackInfo ci) {
        //TODO logic for tick view distance
        ci.cancel();
    }

}
