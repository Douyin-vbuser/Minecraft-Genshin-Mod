//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:ChatGPT(LMArena);DeepSeek
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.shader;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.lwjgl.opengl.GL11;

/**
 * RenderEventHandler 类负责处理渲染相关的事件<br>
 * 管理着色器的应用、窗口大小变化检测和帧缓冲区重置
 */
@SuppressWarnings("unused")
public class RenderEventHandler {
    private static final Minecraft mc = Minecraft.getMinecraft();  // Minecraft客户端实例
    private boolean needsResize = false;  // 标记是否需要调整大小
    private int lastWidth = 0;           // 上次记录的窗口宽度
    private int lastHeight = 0;          // 上次记录的窗口高度

    /**
     * 客户端Tick事件处理<br>
     * 在每个客户端Tick结束时检查窗口大小变化
     * @param event 客户端Tick事件
     */
    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            checkResize();
        }
    }

    /**
     * 检查窗口大小是否发生变化<br>
     * 如果检测到变化，设置需要调整大小的标志
     */
    private void checkResize() {
        if (mc.displayWidth != lastWidth || mc.displayHeight != lastHeight) {
            needsResize = true;
            lastWidth = mc.displayWidth;
            lastHeight = mc.displayHeight;
        }
    }

    /**
     * 世界渲染最后阶段的事件处理<br>
     * 在此阶段应用后处理着色器效果
     * @param event 世界渲染最后事件
     */
    @SubscribeEvent
    public void onRenderGameOverlay(RenderWorldLastEvent event) {
        // 检查是否满足应用着色器的条件
        if (mc.player != null && mc.world != null && ShaderManager.isShaderActive()) {
            // 设置渲染状态
            GlStateManager.depthMask(false);
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            // 如果需要调整大小，重置帧缓冲区
            if (needsResize) {
                resetFramebuffers();
                needsResize = false;
            }

            // 应用着色器
            ShaderManager.applyShader();

            // 恢复渲染状态
            GlStateManager.depthMask(true);
            GlStateManager.enableDepth();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    /**
     * 重置帧缓冲区<br>
     * 删除现有的帧缓冲区对象，为重新创建做准备
     */
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

    /**
     * 清理资源<br>
     * 重置所有状态和缓存
     */
    public void cleanup() {
        resetFramebuffers();
        lastWidth = 0;
        lastHeight = 0;
        needsResize = false;
    }

    // 基础顶点着色器资源位置
    static ResourceLocation baseVert = new ResourceLocation("particulate:shaders/post/base.vert");

    /**
     * 加载默认着色器<br>
     * 注释掉的代码展示了如何加载深度着色器
     */
    public static void loadDefaultShaders() {
//        ShaderManager.loadShaders(
//                baseVert, new ResourceLocation("particulate:shaders/post/depth.frag")
//        );
    }

    /**
     * 重新加载着色器<br>
     * 先清理现有着色器，然后加载默认着色器
     */
    public void reloadShaders() {
        ShaderManager.cleanup();
        loadDefaultShaders();
    }

    /**
     * 切换着色器状态<br>
     * 如果着色器已激活则清理，否则加载默认着色器
     */
    public void toggleShaders() {
        if (ShaderManager.isShaderActive()) {
            ShaderManager.cleanup();
        } else {
            loadDefaultShaders();
        }
    }
}