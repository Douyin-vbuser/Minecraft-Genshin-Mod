package com.vbuser.particulate.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;

import java.io.IOException;

@SuppressWarnings("unused")
public class ShaderManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static ShaderProgram currentShader;
    public static Framebuffer shaderFramebuffer;

    public static void init() {
        cleanup();
        //test line: test whether shader injector run normally.
        //loadShader(new ResourceLocation("particulate:shaders/post/gray.vert"), new ResourceLocation("particulate:shaders/post/gray.frag"));
    }

    public static void loadShader(ResourceLocation vertShader, ResourceLocation fragShader) {
        cleanup();
        try {
            ShaderProgram shader = new ShaderProgram();
            shader.attachVertexShader(vertShader);
            shader.attachFragmentShader(fragShader);
            shader.link();
            currentShader = shader;
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shader", e);
        }
    }

    public static void applyShader() {
        if (currentShader == null || mc.player == null) return;

        Framebuffer originalFramebuffer = mc.getFramebuffer();
        setupFramebuffer();

        try {
            setupRenderState();
            renderToShaderFramebuffer(originalFramebuffer);
            renderToScreen();
        } catch (Exception e) {
            System.err.println("Error applying shader: " + e.getMessage());
        } finally {
            restoreRenderState();
            originalFramebuffer.bindFramebuffer(true);
        }
    }


    private static void renderToShaderFramebuffer(Framebuffer sourceFramebuffer) {
        shaderFramebuffer.bindFramebuffer(true);
        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

        currentShader.use();
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

        sourceFramebuffer.bindFramebufferTexture();
        setupTextureParameters();
        setupShaderUniforms();

        renderFullscreenQuad();
        currentShader.release();
        GlStateManager.bindTexture(0);
    }

    private static void setupRenderState() {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.enableTexture2D();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
    }

    private static void renderToScreen() {
        mc.getFramebuffer().bindFramebuffer(true);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        shaderFramebuffer.bindFramebufferTexture();
        setupTextureParameters();
        renderFullscreenQuad();

        GlStateManager.bindTexture(0);
    }

    private static void setupFramebuffer() {
        if (shaderFramebuffer == null ||
                shaderFramebuffer.framebufferWidth != mc.displayWidth ||
                shaderFramebuffer.framebufferHeight != mc.displayHeight) {
            if (shaderFramebuffer != null) {
                shaderFramebuffer.deleteFramebuffer();
            }
            shaderFramebuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
            shaderFramebuffer.setFramebufferFilter(GL11.GL_LINEAR);
        }
    }

    private static void setupTextureParameters() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
    }

    private static void setupShaderUniforms() {
        if (currentShader != null) {
            currentShader.setUniform1i("texture", 0);
            currentShader.setUniform2f("resolution", mc.displayWidth, mc.displayHeight);
            currentShader.setUniform1f("time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
        }
    }

    private static void renderFullscreenQuad() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, -1.0D, 1.0D);

        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.0D, mc.displayHeight, 0.0D).tex(0.0D, 0.0D).endVertex();
        buffer.pos(mc.displayWidth, mc.displayHeight, 0.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(mc.displayWidth, 0.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();

        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
    }

    private static void restoreRenderState() {
        GlStateManager.bindTexture(0);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        GlStateManager.enableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableTexture2D();

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        GlStateManager.popAttrib();
        GlStateManager.popMatrix();

        mc.getFramebuffer().bindFramebuffer(true);
    }

    public static void cleanup() {
        if (currentShader != null) {
            currentShader.delete();
            currentShader = null;
        }
        if (shaderFramebuffer != null) {
            shaderFramebuffer.deleteFramebuffer();
            shaderFramebuffer = null;
        }
    }

    public static ShaderProgram getCurrentShader() {
        return currentShader;
    }

    public static boolean isShaderActive() {
        return currentShader != null && !currentShader.isDeleted();
    }
}