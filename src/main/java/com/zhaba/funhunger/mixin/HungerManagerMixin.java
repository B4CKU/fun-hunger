package com.zhaba.funhunger.mixin;

import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HungerManager.class)
public class HungerManagerMixin {

    //This mixin does a couple things, and is arguably the most important part of the mod.
    // - Removes starvation mechanics
    // - Removes exhaustion hunger penalty
    // - Changes regeneration mechanics - healing numbers and the delay have been adjusted and based on player's
    //   saturation (see "addOverride" below); regeneration procs now reduce the food var rather than saturation.
    @Inject(method = "update", at = @At("HEAD"), cancellable = true)
    private void updateOverride(PlayerEntity player, CallbackInfo ci) {
        HungerManager hungerManager = (HungerManager)(Object) this; //a smart way of referencing "this" in the context of a mixin

        //line below is a slightly modified version of the original update method, stripped of the saturation boost
        if (player.canFoodHeal() && hungerManager.getFoodLevel() > 0) {
            int hungerManagerFoodTickTimer = ((HungerManagerAccessor) hungerManager).getFoodTickTimer();

            if (hungerManagerFoodTickTimer >= getRegenerationTimer(hungerManager.getSaturationLevel())) {
                player.heal(1.0F);
                hungerManager.setFoodLevel(hungerManager.getFoodLevel() - 1);
                ((HungerManagerAccessor) hungerManager).setFoodTickTimer(0);
                //TODO: change amount of food consumed per healing/amount of health restored per pip of food
            }
            else {
                ((HungerManagerAccessor) hungerManager).setFoodTickTimer(hungerManagerFoodTickTimer + 1);
            }
        }


        ci.cancel(); //equivalent of "return;"
    }

    //Helper method for calculating the delay between regeneration procs.
    private static float getRegenerationTimer(float saturation) {
        return 20 + (saturation * 4);
    }

    //This mixin handles adding food values upon consuming food.
    //While the way food variable is added itself isn't changed, the way saturation works is much different.
    //Now, player's "saturation" variable only stores the average of all the food items used to fill the food bar.
    @Inject(method = "add", at=@At("HEAD"), cancellable = true)
    private void addOverride(int food, float saturationModifier, CallbackInfo ci) {
        HungerManager hungerManager = (HungerManager)(Object) this;

        int old_food = hungerManager.getFoodLevel();
        float old_saturation = hungerManager.getSaturationLevel();

        //variable handling how much food was ACTUALLY added to our bar, ie: not counting overeating
        int delta_food = food - Math.max(food + old_food - max_food, 0);

        //the line below averages the food
        hungerManager.setSaturationLevel( ((old_saturation * old_food) + (food * saturationModifier * 2.0F * delta_food)) / (old_food + food) );
        hungerManager.setFoodLevel(Math.min(old_food + food, max_food));

        ci.cancel();
    }

    //Variable that describes the highest value the foodLevel can have.
    public int max_food = 10;
}
