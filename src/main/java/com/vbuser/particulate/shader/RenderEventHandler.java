package com.vbuser.particulate.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

@SuppressWarnings("unused")
public class RenderEventHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private boolean needsResize = false;
    private int lastWidth = 0;
    private int lastHeight = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            checkResize();
        }
    }

    private void checkResize() {
        if (mc.displayWidth != lastWidth || mc.displayHeight != lastHeight) {
            needsResize = true;
            lastWidth = mc.displayWidth;
            lastHeight = mc.displayHeight;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderWorldLastEvent event) {
        if (mc.player != null && mc.world != null && ShaderManager.isShaderActive()) {
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            if (needsResize) {
                resetFramebuffers();
                needsResize = false;
            }

            ShaderManager.applyShader();

            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    private void resetFramebuffers() {
        if (ShaderManager.pingPongBuffers != null) {
            for (int i = 0; i < ShaderManager.pingPongBuffers.length; i++) {
                if (ShaderManager.pingPongBuffers[i] != null) {
                    ShaderManager.pingPongBuffers[i].deleteFramebuffer();
                    ShaderManager.pingPongBuffers[i] = null;
                }
            }
        }
    }

    public void cleanup() {
        resetFramebuffers();
        lastWidth = 0;
        lastHeight = 0;
        needsResize = false;
    }

    static ResourceLocation baseVert = new ResourceLocation("particulate:shaders/post/base.vert");

    public static void loadDefaultShaders() {
//        ShaderManager.loadShaders(
//                baseVert, new ResourceLocation("particulate:shaders/post/depth.frag")
//        );
    }

    public void reloadShaders() {
        ShaderManager.cleanup();
        loadDefaultShaders();
    }

    public void toggleShaders() {
        if (ShaderManager.isShaderActive()) {
            ShaderManager.cleanup();
        } else {
            loadDefaultShaders();
        }
    }
}