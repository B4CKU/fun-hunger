package com.zhaba.funhunger.mixin;

import net.minecraft.entity.player.HungerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HungerManager.class)
public interface HungerManagerAccessor {

    @Accessor("foodTickTimer")
    int getFoodTickTimer();

    @Accessor("foodTickTimer")
    void setFoodTickTimer(int foodTickTimer);
}