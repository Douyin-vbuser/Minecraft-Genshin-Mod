package com.vbuser.particulate.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL13;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class ShaderManager {
    private static final Minecraft mc = Minecraft.getMinecraft();
    private static final List<ShaderProgram> shaderPasses = new ArrayList<>();
    public static Framebuffer[] pingPongBuffers = new Framebuffer[2];

    public static void init() {
        cleanup();
        RenderEventHandler.loadDefaultShaders();
    }

    public static void loadShaders(ResourceLocation... shaderPairs) {
        cleanup();
        try {
            for (int i = 0; i < shaderPairs.length; i += 2) {
                ShaderProgram shader = new ShaderProgram();
                shader.attachVertexShader(shaderPairs[i]);
                shader.attachFragmentShader(shaderPairs[i + 1]);
                shader.link();
                shaderPasses.add(shader);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load shaders", e);
        }
    }

    public static void applyShader() {
        if (shaderPasses.isEmpty() || mc.player == null) return;

        Framebuffer originalFramebuffer = mc.getFramebuffer();
        setupFramebuffers();

        try {
            setupRenderState();

            if (shaderPasses.size() == 1) {
                renderPass(shaderPasses.get(0), originalFramebuffer, originalFramebuffer);
            } else {
                int readBuffer = 0;
                int writeBuffer = 1;

                renderPass(shaderPasses.get(0), originalFramebuffer, pingPongBuffers[writeBuffer]);

                for (int i = 1; i < shaderPasses.size() - 1; i++) {
                    int temp = readBuffer;
                    readBuffer = writeBuffer;
                    writeBuffer = temp;

                    renderPass(shaderPasses.get(i), pingPongBuffers[readBuffer], pingPongBuffers[writeBuffer]);
                }

                renderPass(shaderPasses.get(shaderPasses.size() - 1), pingPongBuffers[writeBuffer], originalFramebuffer);
            }

        } catch (Exception e) {
            System.err.println("Error applying shaders: " + e.getMessage());
        } finally {
            restoreRenderState();
            originalFramebuffer.bindFramebuffer(true);
        }
    }

    private static void renderPass(ShaderProgram shader, Framebuffer input, Framebuffer output) {
        if (input == output) {
            setupFramebuffers();
            Framebuffer temp = pingPongBuffers[0];

            temp.bindFramebuffer(true);
            GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            input.bindFramebufferTexture();
            setupTextureParameters();
            renderFullscreenQuadSimple();
            GlStateManager.bindTexture(0);

            output.bindFramebuffer(true);
            GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

            shader.use();
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
            temp.bindFramebufferTexture();
        } else {
            output.bindFramebuffer(true);
            GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);

            shader.use();
            GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

            input.bindFramebufferTexture();

        }
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ZERO);
        setupTextureParameters();
        setupShaderUniforms(shader);
        renderFullscreenQuad();
        shader.release();
        GlStateManager.bindTexture(0);
    }

    private static void renderFullscreenQuadSimple() {
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

    private static void setupRenderState() {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();

        GlStateManager.disableAlpha();
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);

        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
    }

    private static void setupFramebuffers() {
        for (int i = 0; i < 2; i++) {
            if (pingPongBuffers[i] == null ||
                    pingPongBuffers[i].framebufferWidth != mc.displayWidth ||
                    pingPongBuffers[i].framebufferHeight != mc.displayHeight) {
                if (pingPongBuffers[i] != null) {
                    pingPongBuffers[i].deleteFramebuffer();
                }
                pingPongBuffers[i] = new Framebuffer(mc.displayWidth, mc.displayHeight, false);
                pingPongBuffers[i].setFramebufferFilter(GL11.GL_LINEAR);
            }
        }
    }

    private static void setupTextureParameters() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_LINEAR);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
    }

    private static void setupShaderUniforms(ShaderProgram shader) {
        shader.setUniform1i("texture", 0);
        shader.setUniform2f("resolution", mc.displayWidth, mc.displayHeight);
        shader.setUniform1f("time", (float)(System.currentTimeMillis() % 100000L) / 1000.0F);
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
    }

    public static void cleanup() {
        for (ShaderProgram shader : shaderPasses) {
            if (shader != null) {
                shader.delete();
            }
        }
        shaderPasses.clear();

        for (int i = 0; i < 2; i++) {
            if (pingPongBuffers[i] != null) {
                pingPongBuffers[i].deleteFramebuffer();
                pingPongBuffers[i] = null;
            }
        }
    }

    public static List<ShaderProgram> getShaderPasses() {
        return shaderPasses;
    }

    public static boolean isShaderActive() {
        return !shaderPasses.isEmpty() && shaderPasses.stream().noneMatch(ShaderProgram::isDeleted);
    }
}
