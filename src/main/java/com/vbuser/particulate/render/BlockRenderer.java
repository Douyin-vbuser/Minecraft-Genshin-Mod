package com.vbuser.particulate.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlockRenderer {

    static ConcurrentMap<BlockPos, IBlockState> map = new ConcurrentHashMap<>();
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
        clearVisibilityCache();
        map = new ConcurrentHashMap<>(value);

        long[] posArray = new long[value.size()];
        int i = 0;
        for (BlockPos pos : value.keySet()) {
            posArray[i++] = pos.toLong();
        }
        NativeBlockRenderer.updateMap(posArray);

        setLoaded(!value.isEmpty());
    }

    @SubscribeEvent
    public void onRenderTick(RenderWorldLastEvent event) {
        if (loaded && map != null) {
            Minecraft mc = Minecraft.getMinecraft();
            Entity viewEntity = mc.getRenderViewEntity();
            if (viewEntity == null) return;

            double entityX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * event.getPartialTicks();
            double entityY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * event.getPartialTicks();
            double entityZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * event.getPartialTicks();

            double viewX = entityX;
            double viewY = entityY;
            double viewZ = entityZ;

            if (mc.gameSettings.thirdPersonView != 0) {
                float distance = 4.0F;

                float yaw = viewEntity.rotationYaw;
                float pitch = viewEntity.rotationPitch;

                double offsetX = -Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));
                double offsetY = -Math.sin(Math.toRadians(pitch));
                double offsetZ = Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch));

                double length = Math.sqrt(offsetX * offsetX + offsetY * offsetY + offsetZ * offsetZ);
                offsetX = offsetX / length * distance;
                offsetY = offsetY / length * distance;
                offsetZ = offsetZ / length * distance;

                if (mc.gameSettings.thirdPersonView == 2) {
                    offsetX = -offsetX;
                    offsetY = -offsetY;
                    offsetZ = -offsetZ;
                }

                viewX = entityX + offsetX;
                viewY = entityY + offsetY;
                viewZ = entityZ + offsetZ;
            }

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
            if (!copyMap.isEmpty()) {
                for (Map.Entry<BlockPos, IBlockState> entry : copyMap.entrySet()) {
                    BlockPos pos = entry.getKey();
                    IBlockState state = entry.getValue();

                    if (isBlockVisible(pos)) {
                        dispatcher.renderBlock(state, pos, world, bufferBuilder);
                    }
                }
            }

            tessellator.draw();
            GlStateManager.popMatrix();
        }
    }

    private static boolean isBlockInFrustum(BlockPos pos) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        return frustum.isBoundingBoxInFrustum(aabb);
    }

    private static boolean isBlockVisible(BlockPos blockPos) {
        if(!isBlockInFrustum(blockPos)){
            return false;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayerSP player = mc.player;

        Vec3d playerEyes = player.getPositionEyes(1.0f);
        Vec3d blockCenter = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        RayTraceResult result = mc.world.rayTraceBlocks(playerEyes, blockCenter, false, true, false);

        if(!(result == null || result.getBlockPos().equals(blockPos))){
            return false;
        }

        return NativeBlockRenderer.visibleInMap(
                playerEyes.x, playerEyes.y, playerEyes.z,
                blockCenter.x, blockCenter.y, blockCenter.z
        );
    }

    public static void clearVisibilityCache() {
        NativeBlockRenderer.clearVisibilityCache();
    }
}