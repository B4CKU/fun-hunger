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
        float currentSaturation = hungerManager.getSaturationLevel();

        //line below is a slightly modified version of the original update method, stripped of the saturation boost
        if (player.canFoodHeal() && hungerManager.getFoodLevel() >= getHungerDrain(currentSaturation)) {
            int currentFoodTickTimer = ((HungerManagerAccessor) hungerManager).getFoodTickTimer();

            if (currentFoodTickTimer >= getRegenerationTimer(currentSaturation)) {
                player.heal(1.0F);
                hungerManager.setFoodLevel(hungerManager.getFoodLevel() - getHungerDrain(currentSaturation));
                ((HungerManagerAccessor) hungerManager).setFoodTickTimer(0);
            }
            else {
                ((HungerManagerAccessor) hungerManager).setFoodTickTimer(currentFoodTickTimer + 1);
            }
        }

        ci.cancel(); //equivalent of "return;", requires "cancellable = true" in the Inject decorator
    }

    //Helper method for calculating the delay between regeneration procs.
    private static float getRegenerationTimer(float saturation) {
        return 10 + (saturation * 5);
    }

    //Helper method for calculating food points drained by the regeneration procs.
    //I don't have any explanation for why I chose these numbers, I just experimented on Desmos and found a graph
    //that I liked.
    private static int getHungerDrain(float saturation) {
        return Math.max(Math.round(24 / (saturation + 2)), 1);
    }
    //TODO: change the formula. make it slightly less harsh at low saturation, but keep the high saturation values similar

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

        //the line below averages the food and mathClamp keeps it from overflowing or underflowing (is that a word?)
        hungerManager.setSaturationLevel(mathClamp(((old_saturation * old_food) + (food * saturationModifier * 2.0F * delta_food)) / (old_food + food), min_sat, max_sat));
        hungerManager.setFoodLevel(mathClamp(old_food + food, min_food, max_food));

        ci.cancel();
    }

    //Two helper functions for clamping values, just because java for some reason doesn't have one by default.
    private float mathClamp(float value, float min, float max) {
        return Math.max(Math.min(value, max), min);
    }
    private int mathClamp(int value, int min, int max) {
        return Math.max(Math.min(value, max), min);
    }

    //Set of variables describing the range of foodLevel and saturationLevel values, used for preventing invalid values.
    //Probably shouldn't be changed.
    public int max_food = 20;
    public int min_food = 0;
    public int max_sat = 20;
    public int min_sat = 0;

    //Multiplier used on both incoming food values *and* regeneration food drain. Effectively decreases the total
    //food one can have without having to deal with:
    // - preventing food use when not hungry
    // - overeating
    // - mod compatibility
    // - other stuff
    public float hungerMult = 2f;
    //TODO: implement it
}
