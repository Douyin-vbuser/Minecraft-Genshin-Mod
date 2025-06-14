package com.vbuser.particulate.render;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.LinkedHashMap;
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

        return visibleInMap(playerEyes, blockCenter);
    }

    private static class VisibilityCache {
        private static final int MAX_CACHE_SIZE = 1000;
        private static final Map<Long, Boolean> cache = new LinkedHashMap<Long, Boolean>(MAX_CACHE_SIZE, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        };

        private static long createKey(BlockPos from, BlockPos to) {
            return ((long) from.getX() & 0x7FFFL) << 52 |
                    ((long) from.getY() & 0xFFFL) << 40 |
                    ((long) from.getZ() & 0x7FFFL) << 28 |
                    ((long) to.getX() & 0x7FFFL) << 16 |
                    ((long) to.getY() & 0xFFFL) << 4 |
                    ((long) to.getZ() & 0x7FFFL);
        }

        public static void clear() {
            cache.clear();
        }
    }

    private static boolean visibleInMap(Vec3d fromVec, Vec3d toVec) {
        BlockPos fromPos = new BlockPos(fromVec);
        BlockPos toPos = new BlockPos(toVec);

        long cacheKey = VisibilityCache.createKey(fromPos, toPos);
        Boolean cachedResult = VisibilityCache.cache.get(cacheKey);
        if (cachedResult != null) {
            return cachedResult;
        }

        boolean result = checkVisibilityDDA(fromVec, toVec);
        VisibilityCache.cache.put(cacheKey, result);
        return result;
    }

    private static boolean checkVisibilityDDA(Vec3d fromVec, Vec3d toVec) {
        double dx = toVec.x - fromVec.x;
        double dy = toVec.y - fromVec.y;
        double dz = toVec.z - fromVec.z;

        double distance = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (distance < 0.0001) return true;

        dx /= distance;
        dy /= distance;
        dz /= distance;

        double x = fromVec.x;
        double y = fromVec.y;
        double z = fromVec.z;

        double stepX = dx >= 0 ? 1 : -1;
        double stepY = dy >= 0 ? 1 : -1;
        double stepZ = dz >= 0 ? 1 : -1;

        double tMaxX = dx != 0 ? (stepX > 0 ? Math.ceil(x) - x : x - Math.floor(x)) / Math.abs(dx) : Double.MAX_VALUE;
        double tMaxY = dy != 0 ? (stepY > 0 ? Math.ceil(y) - y : y - Math.floor(y)) / Math.abs(dy) : Double.MAX_VALUE;
        double tMaxZ = dz != 0 ? (stepZ > 0 ? Math.ceil(z) - z : z - Math.floor(z)) / Math.abs(dz) : Double.MAX_VALUE;

        double tDeltaX = dx != 0 ? Math.abs(1 / dx) : Double.MAX_VALUE;
        double tDeltaY = dy != 0 ? Math.abs(1 / dy) : Double.MAX_VALUE;
        double tDeltaZ = dz != 0 ? Math.abs(1 / dz) : Double.MAX_VALUE;

        int blocksPassed = 0;
        BlockPos targetPos = new BlockPos(toVec);

        while (true) {
            BlockPos currentPos = new BlockPos(x, y, z);
            if (currentPos.equals(targetPos)) {
                return true;
            }

            if (map.containsKey(currentPos)) {
                blocksPassed++;
                if (blocksPassed > 5) {
                    return false;
                }
            }

            if (Math.abs(x - fromVec.x) > distance ||
                    Math.abs(y - fromVec.y) > distance ||
                    Math.abs(z - fromVec.z) > distance) {
                return false;
            }

            if (tMaxX < tMaxY && tMaxX < tMaxZ) {
                x += stepX;
                tMaxX += tDeltaX;
            } else if (tMaxY < tMaxZ) {
                y += stepY;
                tMaxY += tDeltaY;
            } else {
                z += stepZ;
                tMaxZ += tDeltaZ;
            }
        }
    }

    public static void clearVisibilityCache() {
        VisibilityCache.clear();
    }
}