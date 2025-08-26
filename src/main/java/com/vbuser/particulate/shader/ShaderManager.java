package com.vbuser.particulate.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * ShaderManager 类管理后处理着色器的加载和应用<br>
 * 支持多通道着色器和乒乓缓冲区技术
 */
@SuppressWarnings("unused")
public class ShaderManager {
    private static final Minecraft mc = Minecraft.getMinecraft();  // Minecraft客户端实例
    private static final List<ShaderProgram> shaderPasses = new ArrayList<>();  // 着色器通道列表
    public static Framebuffer[] pingPongBuffers = new Framebuffer[2];  // 乒乓缓冲区
    private static final int[] depthTextures = new int[2];  // 深度纹理

    /**
     * 初始化着色器管理器<br>
     * 清理现有资源并加载默认着色器
     */
    public static void init() {
        cleanup();
        RenderEventHandler.loadDefaultShaders();
    }

    /**
     * 加载着色器对（顶点+片段）<br>
     * 每对资源位置对应一个顶点着色器和一个片段着色器
     * @param shaderPairs 着色器资源位置数组（必须是偶数个）
     */
    public static void loadShaders(ResourceLocation... shaderPairs) {
        cleanup();
        try {
            // 每两个资源位置为一对（顶点+片段）
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

    /**
     * 应用着色器效果<br>
     * 执行多通道着色器渲染
     */
    public static void applyShader() {
        if (shaderPasses.isEmpty() || mc.player == null) return;

        Framebuffer originalFramebuffer = mc.getFramebuffer();  // 原始帧缓冲区
        setupFramebuffers();  // 设置乒乓缓冲区
        copyMainDepthToTextures(originalFramebuffer);  // 复制深度信息到纹理

        try {
            setupRenderState();  // 设置渲染状态

            // 单通道着色器处理
            if (shaderPasses.size() == 1) {
                renderPass(shaderPasses.get(0), originalFramebuffer, originalFramebuffer);
            } else {
                // 多通道着色器处理（使用乒乓缓冲区）
                int readBuffer = 0;
                int writeBuffer = 1;

                // 第一通道：原始缓冲区 -> 乒乓缓冲区
                renderPass(shaderPasses.get(0), originalFramebuffer, pingPongBuffers[writeBuffer]);

                // 中间通道：乒乓缓冲区之间交替
                for (int i = 1; i < shaderPasses.size() - 1; i++) {
                    int temp = readBuffer;
                    readBuffer = writeBuffer;
                    writeBuffer = temp;

                    renderPass(shaderPasses.get(i), pingPongBuffers[readBuffer], pingPongBuffers[writeBuffer]);
                }

                // 最后一通道：乒乓缓冲区 -> 原始缓冲区
                renderPass(shaderPasses.get(shaderPasses.size() - 1), pingPongBuffers[writeBuffer], originalFramebuffer);
            }

        } catch (Exception e) {
            System.err.println("Error applying shaders: " + e.getMessage());
        } finally {
            restoreRenderState();  // 恢复渲染状态
            originalFramebuffer.bindFramebuffer(true);  // 绑定回原始帧缓冲区
        }
    }

    /**
     * 复制主帧缓冲区的深度信息到纹理<br>
     * 用于在着色器中访问深度信息
     * @param mainFbo 主帧缓冲区
     */
    private static void copyMainDepthToTextures(Framebuffer mainFbo) {
        int prevFBO = GL11.glGetInteger(GL30.GL_FRAMEBUFFER_BINDING);
        int prevTexture = GL11.glGetInteger(GL11.GL_TEXTURE_BINDING_2D);

        // 为每个乒乓缓冲区创建深度纹理
        for (int i = 0; i < pingPongBuffers.length; i++) {
            if (pingPongBuffers[i] != null) {
                GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextures[i]);
                mainFbo.bindFramebuffer(false);
                GL11.glCopyTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT,
                        0, 0, mainFbo.framebufferWidth, mainFbo.framebufferHeight, 0);
            }
        }

        // 恢复之前的绑定状态
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, prevTexture);
        GL30.glBindFramebuffer(GL30.GL_FRAMEBUFFER, prevFBO);
    }

    /**
     * 渲染单个着色器通道
     * @param shader 着色器程序
     * @param input 输入帧缓冲区
     * @param output 输出帧缓冲区
     */
    private static void renderPass(ShaderProgram shader, Framebuffer input, Framebuffer output) {
        output.bindFramebuffer(true);  // 绑定输出帧缓冲区
        GlStateManager.clearColor(0.0F, 0.0F, 0.0F, 0.0F);  // 设置清除颜色
        GlStateManager.clear(GL11.GL_COLOR_BUFFER_BIT);  // 清除颜色缓冲区

        shader.use();  // 使用着色器程序

        // 绑定颜色纹理
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        input.bindFramebufferTexture();

        // 绑定深度纹理
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE1);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextures[0]);

        setupTextureParameters();  // 设置纹理参数
        setupShaderUniforms(shader);  // 设置着色器统一变量
        renderFullscreenQuad();  // 渲染全屏四边形

        shader.release();  // 释放着色器程序
        GlStateManager.bindTexture(0);  // 解除纹理绑定
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
        GlStateManager.bindTexture(0);
    }

    /**
     * 渲染全屏四边形<br>
     * 用于应用后处理效果
     */
    private static void renderFullscreenQuad() {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        // 设置正交投影矩阵
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();
        GlStateManager.ortho(0.0D, mc.displayWidth, mc.displayHeight, 0.0D, -1.0D, 1.0D);

        // 设置模型视图矩阵
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.pushMatrix();
        GlStateManager.loadIdentity();

        // 构建全屏四边形
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        buffer.pos(0.0D, mc.displayHeight, 0.0D).tex(0.0D, 0.0D).endVertex();
        buffer.pos(mc.displayWidth, mc.displayHeight, 0.0D).tex(1.0D, 0.0D).endVertex();
        buffer.pos(mc.displayWidth, 0.0D, 0.0D).tex(1.0D, 1.0D).endVertex();
        buffer.pos(0.0D, 0.0D, 0.0D).tex(0.0D, 1.0D).endVertex();
        tessellator.draw();

        // 恢复矩阵状态
        GlStateManager.matrixMode(GL11.GL_PROJECTION);
        GlStateManager.popMatrix();
        GlStateManager.matrixMode(GL11.GL_MODELVIEW);
        GlStateManager.popMatrix();
    }

    /**
     * 设置渲染状态<br>
     * 为后处理渲染配置OpenGL状态
     */
    private static void setupRenderState() {
        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        // 禁用不必要的功能
        GlStateManager.disableBlend();
        GlStateManager.disableLighting();
        GlStateManager.disableFog();
        GlStateManager.disableDepth();
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();

        // 设置Alpha测试
        GlStateManager.disableAlpha();
        GlStateManager.alphaFunc(GL11.GL_ALWAYS, 0);

        // 设置颜色和活动纹理单元
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);
    }

    /**
     * 设置帧缓冲区<br>
     * 创建或重新创建乒乓缓冲区
     */
    private static void setupFramebuffers() {
        for (int i = 0; i < 2; i++) {
            // 检查是否需要重新创建帧缓冲区
            if (pingPongBuffers[i] == null ||
                    pingPongBuffers[i].framebufferWidth != mc.displayWidth ||
                    pingPongBuffers[i].framebufferHeight != mc.displayHeight) {
                // 删除旧的帧缓冲区和纹理
                if (pingPongBuffers[i] != null) {
                    pingPongBuffers[i].deleteFramebuffer();
                    GL11.glDeleteTextures(depthTextures[i]);
                }
                // 创建新的帧缓冲区
                pingPongBuffers[i] = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
                pingPongBuffers[i].setFramebufferFilter(GL11.GL_LINEAR);
                createDepthTexture(i);  // 创建深度纹理
            }
        }
    }

    /**
     * 创建深度纹理
     * @param index 纹理索引
     */
    private static void createDepthTexture(int index) {
        depthTextures[index] = GL11.glGenTextures();  // 生成纹理ID
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, depthTextures[index]);
        // 创建深度纹理
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_DEPTH_COMPONENT,
                mc.displayWidth, mc.displayHeight,
                0, GL11.GL_DEPTH_COMPONENT, GL11.GL_FLOAT, (ByteBuffer) null);
        setupTextureParameters();  // 设置纹理参数
    }

    /**
     * 设置纹理参数<br>
     * 配置纹理的过滤和环绕方式
     */
    private static void setupTextureParameters() {
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL14.GL_TEXTURE_COMPARE_MODE, GL11.GL_NONE);
    }

    /**
     * 设置着色器统一变量<br>
     * 将常用参数传递给着色器
     * @param shader 着色器程序
     */
    private static void setupShaderUniforms(ShaderProgram shader) {
        shader.setUniform1i("colorTexture", 0);  // 颜色纹理单元
        shader.setUniform1i("depthTexture", 1);  // 深度纹理单元
        shader.setUniform2f("resolution", mc.displayWidth, mc.displayHeight);  // 屏幕分辨率
        shader.setUniform1f("time", (float) (System.currentTimeMillis() % 100000L) / 1000.0F);  // 时间参数
        shader.setUniform1f("near", 0.05F);  // 近裁剪平面
        shader.setUniform1f("far", 1000.0F);  // 远裁剪平面
    }

    /**
     * 恢复渲染状态<br>
     * 将OpenGL状态恢复到应用着色器之前的状态
     */
    private static void restoreRenderState() {
        GlStateManager.bindTexture(0);  // 解除纹理绑定
        GlStateManager.setActiveTexture(GL13.GL_TEXTURE0);

        // 重新启用常用功能
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);

        GlStateManager.enableLighting();
        GlStateManager.enableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
        GlStateManager.enableTexture2D();

        // 重置颜色
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

        // 恢复属性
        GlStateManager.popAttrib();
        GlStateManager.popMatrix();
    }

    /**
     * 清理资源<br>
     * 删除所有着色器程序、帧缓冲区和纹理
     */
    public static void cleanup() {
        // 删除着色器程序
        for (ShaderProgram shader : shaderPasses) {
            if (shader != null) {
                shader.delete();
            }
        }
        shaderPasses.clear();

        // 删除帧缓冲区和纹理
        for (int i = 0; i < 2; i++) {
            if (pingPongBuffers[i] != null) {
                pingPongBuffers[i].deleteFramebuffer();
                pingPongBuffers[i] = null;
            }
            if (depthTextures[i] != 0) {
                GL11.glDeleteTextures(depthTextures[i]);
                depthTextures[i] = 0;
            }
        }
    }

    /**
     * 获取着色器通道列表
     * @return 着色器通道列表
     */
    public static List<ShaderProgram> getShaderPasses() {
        return shaderPasses;
    }

    /**
     * 检查是否有活动的着色器
     * @return 如果有未删除的着色器则返回true
     */
    public static boolean isShaderActive() {
        return !shaderPasses.isEmpty() && shaderPasses.stream().noneMatch(ShaderProgram::isDeleted);
    }
}