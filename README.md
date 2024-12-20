# Fun Hunger
---
## Overview
This mod aims to rework Minecraft's hunger system, to make it less tedious and give different kinds of food different uses, without being too overpowered.
---
## Changes
- Removed starving.
- Exhaustion mechanics (saturation drain from actions like jumping, sprinting or mining) removed. 
- Sprint is available regardless of player's hunger; now unavailable while below 3 hearts of health.
- Saturation of recently eaten food increases amount of health recovered and decreases the regeneration speed.
- Decreased maximum hunger by 50%; overall healing from food decreased.
---
## WIP Features
- Prevent eating while at 10 hunger.
- Make eating less filling (not saturating!) foods faster.
- Finish the healing formula (HungerManagerMixin.class).
- Implement interrupting eating if hurt.
- HUD tweaks.
- Very slow passive regeneration (add that to the updateOverride?).
- Make sure exhaustion gets reset every now and then - even though it's unused, we don't want it to get ridiculously high, because that could spell some problems.