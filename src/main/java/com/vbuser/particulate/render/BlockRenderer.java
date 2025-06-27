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

import java.util.*;

public class BlockRenderer {

    private static volatile Map<BlockPos, IBlockState> snapshot = Collections.emptyMap();
    private static boolean loaded;
    private static Frustum frustum;
    private static final List<BlockPos> visibleBlocks = new ArrayList<>();
    private static final Map<BlockPos, IBlockState> EMPTY_MAP = Collections.emptyMap();

    static Minecraft mc;
    static EntityPlayerSP player;

    public static void setLoaded(boolean value) {
        loaded = value;
    }

    public static Map<BlockPos, IBlockState> getMap() {
        return snapshot;
    }

    public static void setMap(Map<BlockPos, IBlockState> value) {
        setLoaded(false);
        clearVisibilityCache();

        snapshot = value.isEmpty() ? EMPTY_MAP : Collections.unmodifiableMap(new HashMap<>(value));

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

        double[] viewPosition = calculateViewPosition(viewEntity, event.getPartialTicks(), mc.gameSettings.thirdPersonView);
        double viewX = viewPosition[0];
        double viewY = viewPosition[1];
        double viewZ = viewPosition[2];

        if (frustum == null) {
            frustum = new Frustum();
        }
        frustum.setPosition(viewX, viewY, viewZ);

        GlStateManager.pushMatrix();
        GlStateManager.translate(-viewX, -viewY, -viewZ);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuffer();
        bufferBuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.BLOCK);

        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();
        World world = mc.world;

        visibleBlocks.clear();
        Map<BlockPos, IBlockState> currentSnapshot = snapshot;

        for (BlockPos pos : currentSnapshot.keySet()) {
            if (isBlockVisible(pos)) {
                visibleBlocks.add(pos);
            }
        }

        if (!visibleBlocks.isEmpty()) {
            for (BlockPos pos : visibleBlocks) {
                IBlockState state = currentSnapshot.get(pos);
                dispatcher.renderBlock(state, pos, world, bufferBuilder);
            }
        }

        tessellator.draw();
        GlStateManager.popMatrix();
    }

    private static final ThreadLocal<double[]> viewPosCache = ThreadLocal.withInitial(() -> new double[3]);

    private static double[] calculateViewPosition(Entity viewEntity, float partialTicks, int thirdPersonView) {
        double[] result = viewPosCache.get();
        double entityX = viewEntity.lastTickPosX + (viewEntity.posX - viewEntity.lastTickPosX) * partialTicks;
        double entityY = viewEntity.lastTickPosY + (viewEntity.posY - viewEntity.lastTickPosY) * partialTicks;
        double entityZ = viewEntity.lastTickPosZ + (viewEntity.posZ - viewEntity.lastTickPosZ) * partialTicks;

        if (thirdPersonView == 0) {
            result[0] = entityX;
            result[1] = entityY;
            result[2] = entityZ;
            return result;
        }

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

        if (thirdPersonView == 2) {
            offsetX = -offsetX;
            offsetY = -offsetY;
            offsetZ = -offsetZ;
        }

        result[0] = entityX + offsetX;
        result[1] = entityY + offsetY;
        result[2] = entityZ + offsetZ;
        return result;
    }

    private static boolean isBlockInFrustum(BlockPos pos) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        return frustum.isBoundingBoxInFrustum(aabb);
    }

    private static boolean isBlockVisible(BlockPos blockPos) {
        if (!isBlockInFrustum(blockPos)) {
            return false;
        }

        Vec3d playerEyes = player.getPositionEyes(1.0f);
        Vec3d blockCenter = new Vec3d(blockPos.getX() + 0.5, blockPos.getY() + 0.5, blockPos.getZ() + 0.5);

        RayTraceResult result = mc.world.rayTraceBlocks(playerEyes, blockCenter, false, true, false);

        if (result != null && !result.getBlockPos().equals(blockPos)) {
            return false;
        }

        //return true;
        return NativeBlockRenderer.visibleInMap(
                playerEyes.x, playerEyes.y, playerEyes.z,
                blockCenter.x, blockCenter.y, blockCenter.z
        );
    }

    public static void clearVisibilityCache() {
        NativeBlockRenderer.clearVisibilityCache();
    }
}