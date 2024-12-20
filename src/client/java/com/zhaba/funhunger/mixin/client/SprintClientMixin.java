package com.zhaba.funhunger.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class SprintClientMixin {

    @Inject(method = "canSprint", at = @At("TAIL"), cancellable = true)
    private void sprintingOverride(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity)(Object) this;
        cir.setReturnValue(player.hasVehicle() || player.getHealth() > 6.0F || player.getAbilities().allowFlying);
    }

}
