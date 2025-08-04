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

public class BlockRenderer {

    private static boolean loaded;

    private static final ConcurrentHashMap<BlockPos, IBlockState> snapshot = new ConcurrentHashMap<>();

    static Minecraft mc;
    static EntityPlayerSP player;

    public static void addBlock(BlockPos pos, IBlockState state) {
        snapshot.put(pos, state);
        loaded = true;
    }

    public static void removeBlock(BlockPos pos) {
        snapshot.remove(pos);
        colorCache.remove(pos);
        loaded = !snapshot.isEmpty();
    }

    private long lastCacheUpdate = 0;
    private static final long CACHE_UPDATE_INTERVAL = 1000;

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        if (mc == null && Minecraft.getMinecraft().player != null) {
            mc = Minecraft.getMinecraft();
            player = mc.player;
        }

        if (!loaded || snapshot.isEmpty()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        Entity viewEntity = mc.getRenderViewEntity();
        if (viewEntity == null) return;

        double entityX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * event.getPartialTicks();
        double entityY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * event.getPartialTicks();
        double entityZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * event.getPartialTicks();

        GlStateManager.pushMatrix();
        GlStateManager.translate(-entityX, -entityY, -entityZ);
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        renderAllBlocks(bufferBuilder);

        tessellator.draw();

        GlStateManager.enableLighting();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }

    private void renderAllBlocks(BufferBuilder bufferBuilder) {
        World world = mc.world;
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheUpdate > CACHE_UPDATE_INTERVAL) {
            colorCache.clear();
            lastCacheUpdate = currentTime;
        }

        for (BlockPos pos : snapshot.keySet()) {
            IBlockState state = snapshot.get(pos);
            if (state == null) continue;

            CachedQuadData cachedData = getCachedQuadData(pos, state, world, dispatcher);

            Vec3d offset = state.getOffset(world, pos);
            double x = pos.getX() + offset.x;
            double y = pos.getY() + offset.y;
            double z = pos.getZ() + offset.z;

            for (CachedQuad cachedQuad : cachedData.quads) {
                addQuadToBuffer(bufferBuilder, cachedQuad, x, y, z);
            }
        }
    }

    private void addQuadToBuffer(BufferBuilder bufferBuilder, CachedQuad cachedQuad, double x, double y, double z) {
        bufferBuilder.addVertexData(cachedQuad.vertexData);

        if (cachedQuad.hasTint && cachedQuad.color != null) {
            float r = cachedQuad.color[0];
            float g = cachedQuad.color[1];
            float b = cachedQuad.color[2];
            bufferBuilder.putColorMultiplier(r, g, b, 4);
            bufferBuilder.putColorMultiplier(r, g, b, 3);
            bufferBuilder.putColorMultiplier(r, g, b, 2);
            bufferBuilder.putColorMultiplier(r, g, b, 1);
        } else {
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 4);
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 3);
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 2);
            bufferBuilder.putColorMultiplier(1.0F, 1.0F, 1.0F, 1);
        }

        bufferBuilder.putPosition(x, y, z);
    }

    private static class CachedQuad {
        final int[] vertexData;
        final boolean hasTint;
        final float[] color;

        CachedQuad(int[] vertexData, boolean hasTint, float[] color) {
            this.vertexData = vertexData;
            this.hasTint = hasTint;
            this.color = color;
        }
    }

    private static class CachedQuadData {
        final List<CachedQuad> quads;

        CachedQuadData(List<CachedQuad> quads) {
            this.quads = quads;
        }
    }

    private static final Map<BlockPos, CachedQuadData> colorCache = new ConcurrentHashMap<>();

    private CachedQuadData getCachedQuadData(BlockPos pos, IBlockState state, World world, BlockRendererDispatcher dispatcher) {
        return colorCache.computeIfAbsent(pos, p -> buildCachedQuads(state, world, pos, dispatcher));
    }

    private CachedQuadData buildCachedQuads(IBlockState state, World world, BlockPos pos, BlockRendererDispatcher dispatcher) {
        List<CachedQuad> cachedQuads = new ArrayList<>();
        IBakedModel model = dispatcher.getModelForState(state);

        for (EnumFacing facing : EnumFacing.values()) {
            List<BakedQuad> quads = model.getQuads(state, facing, 0L);
            for (BakedQuad quad : quads) {
                cachedQuads.add(createCachedQuad(quad, state, world, pos));
            }
        }

        List<BakedQuad> quads = model.getQuads(state, null, 0L);
        for (BakedQuad quad : quads) {
            cachedQuads.add(createCachedQuad(quad, state, world, pos));
        }

        return new CachedQuadData(cachedQuads);
    }

    private CachedQuad createCachedQuad(BakedQuad quad, IBlockState state, World world, BlockPos pos) {
        boolean hasTint = quad.hasTintIndex();
        float[] color = null;

        if (hasTint) {
            int colorInt = mc.getBlockColors().colorMultiplier(state, world, pos, quad.getTintIndex());
            if (EntityRenderer.anaglyphEnable) {
                colorInt = TextureUtil.anaglyphColor(colorInt);
            }
            color = new float[] {
                    (float)(colorInt >> 16 & 255) / 255.0F,
                    (float)(colorInt >> 8 & 255) / 255.0F,
                    (float)(colorInt & 255) / 255.0F
            };
        }

        return new CachedQuad(quad.getVertexData(), hasTint, color);
    }
}