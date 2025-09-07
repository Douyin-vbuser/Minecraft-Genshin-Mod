//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:ChatGPT(LMArena)
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * BlockRenderer 类负责渲染自定义方块<br>
 * 管理一个方块快照集合，并在世界渲染的最后阶段渲染这些方块<br>
 * 支持方块状态的缓存和优化渲染
 */
public class BlockRenderer {

    private static boolean loaded;  // 标记是否已加载渲染器

    private static final ConcurrentHashMap<BlockPos, IBlockState> snapshot = new ConcurrentHashMap<>();  // 方块位置和状态的快照

    static Minecraft mc;       // Minecraft客户端实例
    static EntityPlayerSP player;  // 玩家实体

    /**
     * 添加方块到渲染列表
     * @param pos 方块位置
     * @param state 方块状态
     */
    public static void addBlock(BlockPos pos, IBlockState state) {
        snapshot.put(pos, state);
        loaded = true;
    }

    /**
     * 从渲染列表中移除方块
     * @param pos 方块位置
     */
    public static void removeBlock(BlockPos pos) {
        snapshot.remove(pos);
        colorCache.remove(pos);
        loaded = !snapshot.isEmpty();
    }

    private long lastCacheUpdate = 0;  // 上次缓存更新时间
    private static final long CACHE_UPDATE_INTERVAL = 1000;  // 缓存更新间隔（毫秒）

    /**
     * 世界渲染最后阶段的事件处理<br>
     * 在此阶段渲染所有自定义方块
     * @param event 世界渲染最后事件
     */
    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        // 初始化Minecraft实例和玩家实体
        if (mc == null && Minecraft.getMinecraft().player != null) {
            mc = Minecraft.getMinecraft();
            player = mc.player;
        }

        // 如果没有方块需要渲染，则返回
        if (!loaded || snapshot.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) return;

        // 计算摄像机位置（考虑部分帧时间插值）
        double entityX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * event.getPartialTicks();
        double entityY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * event.getPartialTicks();
        double entityZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * event.getPartialTicks();

        // 保存当前OpenGL状态
        GlStateManager.pushMatrix();
        // 平移坐标系到摄像机位置
        GlStateManager.translate(-entityX, -entityY, -entityZ);
        GlStateManager.disableLighting();  // 禁用光照
        GlStateManager.enableBlend();      // 启用混合
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);  // 设置混合函数

        // 准备渲染缓冲区
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);  // 开始绘制四边形

        // 渲染所有方块
        renderAllBlocks(bufferBuilder);

        // 完成绘制
        tessellator.draw();

        // 恢复OpenGL状态
        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    /**
     * 渲染所有方块到缓冲区
     * @param bufferBuilder 缓冲区构建器
     */
    private void renderAllBlocks(BufferBuilder bufferBuilder) {
        World world = mc.world;
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();  // 方块渲染分发器

        // 定期清除颜色缓存
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheUpdate > CACHE_UPDATE_INTERVAL) {
            colorCache.clear();
            lastCacheUpdate = currentTime;
        }

        // 遍历所有方块位置
        for (BlockPos pos : snapshot.keySet()) {
            IBlockState state = snapshot.get(pos);
            if (state == null) continue;

            // 获取缓存的方块四边形数据
            CachedQuadData cachedData = getCachedQuadData(pos, state, world, dispatcher);

            // 计算方块偏移
            Vec3d offset = state.getOffset(world, pos);
            double x = pos.getX() + offset.x;
            double y = pos.getY() + offset.y;
            double z = pos.getZ() + offset.z;

            // 将每个四边形添加到缓冲区
            for (CachedQuad cachedQuad : cachedData.quads) {
                addQuadToBuffer(bufferBuilder, cachedQuad, x, y, z);
            }
        }
    }

    /**
     * 将四边形添加到缓冲区
     * @param bufferBuilder 缓冲区构建器
     * @param cachedQuad 缓存的四边形数据
     * @param x 方块x坐标
     * @param y 方块y坐标
     * @param z 方块z坐标
     */
    private void addQuadToBuffer(BufferBuilder bufferBuilder, CachedQuad cachedQuad, double x, double y, double z) {
        // 添加顶点数据
        bufferBuilder.addVertexData(cachedQuad.vertexData);

        // 应用颜色倍增（如果有染色）
        if (cachedQuad.hasTint && cachedQuad.color != null) {
            float r = cachedQuad.color[0];
            float g = cachedQuad.color[1];
            float b = cachedQuad.color[2];
            bufferBuilder.putColorMultiplier(r, g, b, 4);
            bufferBuilder.putColorMultiplier(r, g, b, 3);
            bufferBuilder.putColorMultiplier(r, g, b, 2);
            bufferBuilder.putColorMultiplier(r, g, b, 1);
        } else {
            // 使用默认颜色（白色）
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 4);
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 3);
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 2);
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 1);
        }

        // 设置位置
        bufferBuilder.putPosition(x, y, z);
    }

    /**
     * 缓存的四边形数据类<br>
     * 存储顶点数据、染色信息和颜色值
     */
    private static class CachedQuad {
        final int[] vertexData;  // 顶点数据数组
        final boolean hasTint;   // 是否有染色
        final float[] color;     // 颜色值数组 [r, g, b]

        CachedQuad(int[] vertexData, boolean hasTint, float[] color) {
            this.vertexData = vertexData;
            this.hasTint = hasTint;
            this.color = color;
        }
    }

    /**
     * 缓存的四边形数据集合类<br>
     * 存储一个方块的所有四边形数据
     */
    private static class CachedQuadData {
        final List<CachedQuad> quads;  // 四边形列表

        CachedQuadData(List<CachedQuad> quads) {
            this.quads = quads;
        }
    }

    /**
     * 颜色缓存映射表<br>
     * 键为方块位置，值为对应的四边形数据
     */
    private static final Map<BlockPos, CachedQuadData> colorCache = new ConcurrentHashMap<>();

    /**
     * 获取缓存的四边形数据<br>
     * 如果缓存中不存在，则构建新的缓存数据
     * @param pos 方块位置
     * @param state 方块状态
     * @param world 世界对象
     * @param dispatcher 方块渲染分发器
     * @return 缓存的四边形数据
     */
    private CachedQuadData getCachedQuadData(BlockPos pos, IBlockState state, World world, BlockRendererDispatcher dispatcher) {
        return colorCache.computeIfAbsent(pos, p -> buildCachedQuads(state, world, pos, dispatcher));
    }

    /**
     * 构建缓存的四边形数据
     * @param state 方块状态
     * @param world 世界对象
     * @param pos 方块位置
     * @param dispatcher 方块渲染分发器
     * @return 缓存的四边形数据
     */
    private CachedQuadData buildCachedQuads(IBlockState state, World world, BlockPos pos, BlockRendererDispatcher dispatcher) {
        List<CachedQuad> cachedQuads = new ArrayList<>();
        IBakedModel model = dispatcher.getModelForState(state);  // 获取方块的烘焙模型

        // 处理所有方向的四边形
        for (EnumFacing facing : EnumFacing.values()) {
            List<BakedQuad> quads = model.getQuads(state, facing, 0L);
            for (BakedQuad quad : quads) {
                cachedQuads.add(createCachedQuad(quad, state, world, pos));
            }
        }

        // 处理通用四边形（无特定方向）
        List<BakedQuad> quads = model.getQuads(state, null, 0L);
        for (BakedQuad quad : quads) {
            cachedQuads.add(createCachedQuad(quad, state, world, pos));
        }

        return new CachedQuadData(cachedQuads);
    }

    /**
     * 创建缓存的四边形
     * @param quad 烘焙四边形
     * @param state 方块状态
     * @param world 世界对象
     * @param pos 方块位置
     * @return 缓存的四边形
     */
    private CachedQuad createCachedQuad(BakedQuad quad, IBlockState state, World world, BlockPos pos) {
        boolean hasTint = quad.hasTintIndex();  // 检查是否有染色索引
        float[] color = null;

        // 如果有染色，计算颜色值
        if (hasTint) {
            int colorInt = mc.getBlockColors().colorMultiplier(state, world, pos, quad.getTintIndex());
            if (EntityRenderer.anaglyphEnable) {
                colorInt = TextureUtil.anaglyphColor(colorInt);  // 处理红蓝3D效果
            }
            color = new float[] {
                    (float)(colorInt >> 16 & 255) / 255.0F,  // 红色分量
                    (float)(colorInt >> 8 & 255) / 255.0F,   // 绿色分量
                    (float)(colorInt & 255) / 255.0F         // 蓝色分量
            };
        }

        return new CachedQuad(quad.getVertexData(), hasTint, color);
    }
}