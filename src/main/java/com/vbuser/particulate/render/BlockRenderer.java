package com.vbuser.particulate.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;

public class BlockRenderer {

    static Map<BlockPos,IBlockState> map = new HashMap<>();

    static boolean loaded;

    public static void setLoaded(boolean value){
        loaded = value;
    }

    public static Map<BlockPos,IBlockState> getMap(){
        return map;
    }

    public static void setMap(Map<BlockPos,IBlockState> value){
        setLoaded(false);
        map = value;
        setLoaded(!value.isEmpty());
    }

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event){
        if(loaded){
            for (Map.Entry<BlockPos, IBlockState> entry : map.entrySet()) {
                BlockPos key = entry.getKey();
                IBlockState value = entry.getValue();
                onRenderWorldLast(value, key);
            }
        }
    }

    public static void onRenderWorldLast(IBlockState state, BlockPos blockPos) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        double distanceSq = player.getDistanceSq(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        double maxRenderDistanceSq = mc.gameSettings.renderDistanceChunks * 16 * mc.gameSettings.renderDistanceChunks * 16;

        if (distanceSq > maxRenderDistanceSq) {
            return;
        }

        if (shouldRender(state,blockPos)) {
            renderBlock(state, blockPos);
        }
    }

    private static boolean shouldRender(IBlockState state,BlockPos pos) {
        if(state.getRenderType() == EnumBlockRenderType.INVISIBLE) {
            return false;
        }
        return isBlockVisible_1(pos);
    }

    private static boolean isBlockVisible_1(BlockPos blockPos) {
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        Vec3d playerEyes = player.getPositionEyes(1.0f);
        Vec3d blockCenter = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        RayTraceResult result = mc.world.rayTraceBlocks(playerEyes, blockCenter, false, true, false);

        return result == null || result.getBlockPos().equals(blockPos);
    }

    private static void renderBlock(IBlockState state, BlockPos blockPos) {
        Minecraft mc = Minecraft.getMinecraft();
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();

        World world = mc.world;

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();

        GlStateManager.pushMatrix();
        GlStateManager.enableCull();
        GlStateManager.cullFace(GlStateManager.CullFace.BACK);
        GlStateManager.translate(-mc.getRenderManager().viewerPosX, -mc.getRenderManager().viewerPosY, -mc.getRenderManager().viewerPosZ);

        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        dispatcher.renderBlock(state, blockPos, world, bufferBuilder);

        tessellator.draw();
        GlStateManager.disableCull();
        GlStateManager.popMatrix();
    }
}
