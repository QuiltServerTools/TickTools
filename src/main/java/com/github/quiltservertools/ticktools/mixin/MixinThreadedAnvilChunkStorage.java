package com.github.quiltservertools.ticktools.mixin;

import net.minecraft.server.world.ThreadedAnvilChunkStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ThreadedAnvilChunkStorage.class)
public interface MixinThreadedAnvilChunkStorage {
    @Accessor("watchDistance")
    int getWatchDistance();
}
