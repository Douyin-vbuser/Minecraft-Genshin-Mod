package com.vbuser.genshin.blocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("all")
public class ShuMei extends FlowerBase {

    public static final PropertyInteger COUNT = PropertyInteger.create("count", 1, 4);

    public ShuMei(String name, Material material) {
        super(name, material);
        setDefaultState(this.blockState.getBaseState().withProperty(COUNT, 4));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COUNT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(COUNT, 4 - meta);
    }

    @Override
    public int getMetaFromState(IBlockState state){return 4-state.getValue(COUNT);}

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
        {
            return true;
        }
        else {
            if (state.getValue(COUNT) != 1) {
                //EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.SHUMEI, 1));
                //worldIn.spawnEntity(entityitem);
                //worldIn.playSound(null,pos, SoundsHandler.PICK, SoundCategory.BLOCKS,5,1);
            }
            worldIn.setBlockState(pos, state.withProperty(COUNT, state.getValue(COUNT) == 1 ? 1 : state.getValue(COUNT) - 1));
            worldIn.scheduleUpdate(pos, this, 200);
            return true;
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        worldIn.setBlockState(pos,state.withProperty(COUNT,state.getValue(COUNT)+1));
    }
}