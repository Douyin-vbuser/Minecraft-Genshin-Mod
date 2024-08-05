package com.vbuser.particulate.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class BlockRenderer {

    static Map<BlockPos, IBlockState> map = new HashMap<>();
    static boolean loaded;
    static Frustum frustum;

    public static void setLoaded(boolean value) {
        loaded = value;
    }

    public static Map<BlockPos, IBlockState> getMap() {
        return map;
    }

    public static void setMap(Map<BlockPos, IBlockState> value) {
        setLoaded(false);
        map = value;
        setLoaded(!value.isEmpty());
    }

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        if (loaded && map != null) {
            Minecraft mc = Minecraft.getMinecraft();
            Entity viewEntity = mc.getRenderViewEntity();
            assert viewEntity != null;
            double viewX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * event.getPartialTicks();
            double viewY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * event.getPartialTicks();
            double viewZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * event.getPartialTicks();

            frustum = new Frustum();
            frustum.setPosition(viewX, viewY, viewZ);

            GlStateManager.pushMatrix();
            GlStateManager.translate(-viewX, -viewY, -viewZ);

            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder bufferBuilder = tessellator.getBuffer();
            bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

            BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
            World world = mc.world;

            Map<BlockPos, IBlockState> copyMap = new HashMap<>(map);
            for (Map.Entry<BlockPos, IBlockState> entry : copyMap.entrySet()) {
                BlockPos pos = entry.getKey();
                IBlockState state = entry.getValue();

                if (isBlockInFrustum(pos)) {
                    dispatcher.renderBlock(state, pos, world, bufferBuilder);
                }
            }

            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    private boolean isBlockInFrustum(BlockPos pos) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        return frustum.isBoundingBoxInFrustum(aabb);
    }
}