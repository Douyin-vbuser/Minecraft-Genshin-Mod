package com.vbuser.movement.event;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FOVHandler {
    private static final float[] FOV_VALUES = {30, 40, 50, 60, 70, 80, 90, 100, 110};
    private static float currentFOV = 70;
    private static final float INTERPOLATION_SPEED = 0.5f;

    public static ConcurrentMap<EntityPlayer, Integer> sp = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !event.side.isClient()) return;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null || event.player != mc.player) return;

        int selectedSlot = mc.player.inventory.currentItem;
        float targetFOV = sp.containsKey(mc.player)
                ? sp.get(mc.player)
                : FOV_VALUES[selectedSlot];

        if (Math.abs(currentFOV - targetFOV) > 0.001f) {
            currentFOV += (targetFOV - currentFOV) * INTERPOLATION_SPEED;
            mc.gameSettings.fovSetting = currentFOV;
        }
    }

    public static int getFOV(EntityPlayer player) {
        return sp.containsKey(player) ? sp.get(player) : (int) FOV_VALUES[player.inventory.currentItem];
    }

    EntityPlayer last;

    @SubscribeEvent
    public void onPlayerEnter(TickEvent.ClientTickEvent event) {
        if (last == null && Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.inventory.currentItem = 4;
        }
        last = Minecraft.getMinecraft().player;
    }

    @SubscribeEvent
    public void onPlayerEnterServer(PlayerEvent.PlayerLoggedInEvent event) {
        event.player.inventory.currentItem = 4;
    }
}
