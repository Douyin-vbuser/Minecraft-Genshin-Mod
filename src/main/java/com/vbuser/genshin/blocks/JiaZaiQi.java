package com.vbuser.genshin.blocks;

import com.vbuser.genshin.Main;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

public class JiaZaiQi extends BlockBase{
    public JiaZaiQi(String name, Material material) {
        super(name, material);
        setCreativeTab(Main.JIAN_CAI);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos,this,100);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int range = 30;
        int playerCount = worldIn.getEntitiesWithinAABB(EntityPlayer.class,new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range))).size();
        if (playerCount > 0) {
            worldIn.setBlockState(pos.down(), Blocks.REDSTONE_BLOCK.getDefaultState());
        }
        else{
            worldIn.setBlockState(pos.down(),Blocks.AIR.getDefaultState());
        }
        worldIn.scheduleUpdate(pos,this,100);
    }
}
