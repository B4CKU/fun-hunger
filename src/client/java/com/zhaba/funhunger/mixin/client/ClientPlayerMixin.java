package com.zhaba.funhunger.mixin.client;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerMixin {

    //This mixin changes the hunger requirement of the sprint into a health requirement.
    //Otherwise it's functionally pretty much the same as vanilla
    @Inject(method = "canSprint", at = @At("TAIL"), cancellable = true)
    private void sprintingOverride(CallbackInfoReturnable<Boolean> cir) {
        PlayerEntity player = (PlayerEntity)(Object) this; //a smart way of referencing "this" in the context of a mixin
        cir.setReturnValue(player.hasVehicle() || player.getHealth() > 6.0F || player.getAbilities().allowFlying);
    }

}
