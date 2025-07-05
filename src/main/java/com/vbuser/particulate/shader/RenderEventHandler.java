package com.vbuser.particulate.shader;

import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@SuppressWarnings("unused")
public class RenderEventHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean needsResize = false;
    private int lastWidth = 0;
    private int lastHeight = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (mc.displayWidth != lastWidth || mc.displayHeight != lastHeight) {
                needsResize = true;
                lastWidth = mc.displayWidth;
                lastHeight = mc.displayHeight;
            }
        }
    }


    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent.Post event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL) {
            if (mc.player != null && mc.world != null) {
                if (needsResize) {
                    resetFramebuffer();
                    needsResize = false;
                }
                ShaderManager.applyShader();
            }
        }
    }

    private void resetFramebuffer() {
        if (ShaderManager.shaderFramebuffer != null) {
            ShaderManager.shaderFramebuffer.deleteFramebuffer();
            ShaderManager.shaderFramebuffer = null;
        }
    }

    public void cleanup() {
        resetFramebuffer();
        lastWidth = 0;
        lastHeight = 0;
        needsResize = false;
    }
}
