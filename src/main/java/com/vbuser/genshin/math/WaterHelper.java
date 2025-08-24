package com.vbuser.genshin.math;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class WaterHelper {

    public static boolean isWaterRaw(BlockPos pos, World world) {
        IBlockState state = world.getBlockState(pos);
        return state.getBlock() instanceof BlockLiquid && state.getValue(BlockLiquid.LEVEL) == 0;
    }

    public static boolean isInShallowWaterRaw(EntityPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        if (!isWaterRaw(pos, player.world)) return false;
        BlockPos above = pos.up();
        if (isWaterRaw(above, player.world)) return false;

        BlockPos below = pos.down();
        IBlockState stateBelow = player.world.getBlockState(below);
        return stateBelow.isOpaqueCube() &&
                stateBelow.getCollisionBoundingBox(player.world, below) != Block.NULL_AABB;
    }

    public static boolean isInDeepWaterRaw(EntityPlayer player) {
        BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        if (!isWaterRaw(pos, player.world)) return false;
        BlockPos above = pos.up();
        BlockPos below = pos.down();
        return isWaterRaw(above, player.world) || isWaterRaw(below, player.world);
    }

    public static int getTopWaterBottomY(BlockPos pos, World world) {
        if (!isWaterRaw(pos, world)) return pos.getY();
        BlockPos cur = pos;
        int worldTop = world.getHeight();
        while (cur.getY() + 1 < worldTop && isWaterRaw(cur.up(), world)) {
            cur = cur.up();
        }
        return cur.getY();
    }

    public static int getTopWaterBottomY(EntityPlayer player) {
        return getTopWaterBottomY(new BlockPos(player.posX, player.posY, player.posZ), player.world);
    }
}
