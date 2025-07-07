package com.vbuser.particulate.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
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
        snapshot = value.isEmpty() ? EMPTY_MAP : Collections.unmodifiableMap(new HashMap<>(value));
        setLoaded(!value.isEmpty());
    }

    private final Map<BlockPos, List<BakedQuad>> quadCache = new HashMap<>();
    private long lastCacheUpdate = 0;
    private static final long CACHE_UPDATE_INTERVAL = 1000;
    private static final int BATCH_SIZE = 64;

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

        World world = mc.world;
        BlockRendererDispatcher dispatcher = mc.getBlockRendererDispatcher();

        visibleBlocks.clear();
        Map<BlockPos, IBlockState> currentSnapshot = snapshot;

        for (BlockPos pos : currentSnapshot.keySet()) {
            if (isBlockVisible(pos)) {
                visibleBlocks.add(pos);
            }
        }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastCacheUpdate > CACHE_UPDATE_INTERVAL) {
            quadCache.clear();
            lastCacheUpdate = currentTime;
        }

        List<BlockPos> positions = new ArrayList<>(visibleBlocks);
        for (int i = 0; i < positions.size(); i += BATCH_SIZE) {
            int end = Math.min(i + BATCH_SIZE, positions.size());
            List<BlockPos> batch = positions.subList(i, end);

            for (BlockPos pos : batch) {
                IBlockState state = currentSnapshot.get(pos);
                List<BakedQuad> quads = quadCache.computeIfAbsent(pos, p -> {
                    List<BakedQuad> quadList = new ArrayList<>();
                    IBakedModel model = dispatcher.getModelForState(state);
                    for (EnumFacing facing : EnumFacing.values()) {
                        quadList.addAll(model.getQuads(state, facing, 0L));
                    }
                    quadList.addAll(model.getQuads(state, null, 0L));
                    return quadList;
                });

                Vec3d offset = state.getOffset(world, pos);
                double x = pos.getX() + offset.x;
                double y = pos.getY() + offset.y;
                double z = pos.getZ() + offset.z;

                for (BakedQuad quad : quads) {
                    bufferBuilder.addVertexData(quad.getVertexData());
                    bufferBuilder.putPosition(x, y, z);
                }
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

    private boolean isBlockVisible(BlockPos pos) {
        AxisAlignedBB aabb = new AxisAlignedBB(pos);
        return frustum.isBoundingBoxInFrustum(aabb);
    }
}