package com.vbuser.movement.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class FOVHandler {
    private static final float[] FOV_VALUES = {30, 40, 50, 60, 70, 80, 90, 100, 110};
    private static float currentFOV = 70;
    private static final float INTERPOLATION_SPEED = 0.5f;

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.side.isClient()) {
            EntityPlayer player = event.player;

            int selectedSlot = player.inventory.currentItem;

            float targetFOV = FOV_VALUES[selectedSlot];

            if (currentFOV != targetFOV) {
                currentFOV = leap(currentFOV, targetFOV);

                Minecraft.getMinecraft().gameSettings.fovSetting = currentFOV;
            }
        }
    }

    private float leap(float start, float end) {
        return start + (end - start) * FOVHandler.INTERPOLATION_SPEED;
    }
}
