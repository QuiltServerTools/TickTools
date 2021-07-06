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
        var item = ((ItemEntity)(Object) this);
        var player = item.getEntityWorld().getClosestPlayer(item.getX(), 64, item.getZ(), item.getEntityWorld().getHeight(), false);
        if (player != null && TickToolsManager.getInstance().playerSpecific().containsKey(player.getUuid())) {
            return TickToolsManager.getInstance().playerSpecific().get(player.getUuid()).itemDespawnTicks;
        }
        return TickToolsManager.getInstance().config().itemDespawnTicks;
    }

}
