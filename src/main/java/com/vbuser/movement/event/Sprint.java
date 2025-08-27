package com.vbuser.movement.event;

import com.vbuser.genshin.proxy.ClientProxy;
import com.vbuser.movement.Movement;
import com.vbuser.movement.network.SprintPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Sprint {

    public static ConcurrentMap<EntityPlayer, Boolean> sprintingPlayers = new ConcurrentHashMap<>();
    private boolean last = true;

    @SubscribeEvent
    public void server(TickEvent.ServerTickEvent event) {
        for (ConcurrentMap.Entry<EntityPlayer, Boolean> entry : sprintingPlayers.entrySet()) {
            EntityPlayer player = entry.getKey();
            player.setSprinting(entry.getValue());
        }
    }

    @SubscribeEvent
    public void sneaking(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            event.player.setSneaking(false);
        }
    }

    @SubscribeEvent
    public void enter(PlayerEvent.PlayerLoggedInEvent event) {
        sprintingPlayers.put(event.player, true);
        event.player.setSprinting(true);
        event.player.setSneaking(false);
    }

    @SubscribeEvent
    public void leave(PlayerEvent.PlayerLoggedOutEvent event) {
        sprintingPlayers.remove(event.player);
    }

    @SubscribeEvent
    public void client(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            EntityPlayerSP player = Minecraft.getMinecraft().player;
            if (player != null) {
                player.setSprinting(last);
                player.movementInput.sneak = false;
            } else {
                last = true;
            }
        }
    }

    @SubscribeEvent
    public void key(InputEvent.KeyInputEvent event) {
        EntityPlayerSP player = Minecraft.getMinecraft().player;
        if (player == null) return;

        if (ClientProxy.RUN.isPressed()) {
            last = !last;
            Movement.network.sendToServer(new SprintPacket(last));
            System.out.println("Toggle Sprint " + player.getName() + ": " + last);
            lastSprintState = last;
            trigger = true;
        }
    }

    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean trigger = false;
    private boolean lastSprintState = true;
    private int displayTimer = 0;
    private static final int DISPLAY_TIME = 40;
    private static final int FADE_TIME = 40;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || mc.player == null) return;

        if (trigger) {
            trigger = false;
            displayTimer = DISPLAY_TIME + FADE_TIME;
        }

        if (displayTimer > 0) {
            displayTimer--;
        }
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.TEXT) return;
        if (displayTimer <= 0) return;

        String localized = lastSprintState ?
                I18n.format("hud.sprint.activate") :
                I18n.format("hud.sprint.inactivate");

        ScaledResolution res = new ScaledResolution(mc);
        int width = res.getScaledWidth();
        int height = res.getScaledHeight();

        float baseWidth = 250f / 0.6f;
        float baseHeight = 120f / 0.6f;
        float scaleX = width / baseWidth;
        float scaleY = height / baseHeight;
        float scale = Math.min(scaleX, scaleY);

        scale = (int)(10 * Math.max(0.3f, Math.min(scale, 8.0f))) / 10f;

        int yPos = height / 4;
        int xPos = (int) ((width - mc.fontRenderer.getStringWidth(localized) * scale) / 2f);

        float alpha = 1.0f;
        if (displayTimer <= FADE_TIME) {
            alpha = (float) displayTimer / FADE_TIME;
        }

        GlStateManager.pushMatrix();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                GlStateManager.SourceFactor.ONE,
                GlStateManager.DestFactor.ZERO
        );

        GlStateManager.scale(scale, scale, 1.0f);

        float scaledX = xPos / scale;
        float scaledY = yPos / scale;

        int color = (int) (255 * alpha) << 24 | 0xFFFFFF;

        mc.fontRenderer.drawStringWithShadow(localized, scaledX, scaledY, color);

        GlStateManager.popMatrix();
        GlStateManager.disableBlend();
    }
}