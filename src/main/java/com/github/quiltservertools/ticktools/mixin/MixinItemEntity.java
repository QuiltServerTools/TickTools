package com.github.quiltservertools.ticktools.mixin;

import com.github.quiltservertools.ticktools.TickToolsManager;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEntity.class)
public class MixinItemEntity {
    @ModifyConstant(method = "tick", constant = @Constant(intValue = 6000))
    public int ticktools$setMaximumDespawnValue(int i) {
        return TickToolsManager.getInstance().config().itemDespawnTicks;
    }

}
