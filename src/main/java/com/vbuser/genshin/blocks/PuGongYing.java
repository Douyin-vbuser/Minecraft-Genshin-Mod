package com.vbuser.genshin.blocks;

import com.vbuser.genshin.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("all")
public class PuGongYing extends FlowerBase{

    public static final PropertyInteger STATE = PropertyInteger.create("state",1,3);

    public PuGongYing(String name, Material material){
        super(name,material);
        setDefaultState(blockState.getBaseState().withProperty(STATE,3));
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, STATE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(STATE, 3 - meta);
    }

    @Override
    public int getMetaFromState(IBlockState state){return 3-state.getValue(STATE);}

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if(playerIn.getHeldItemMainhand().getItem()==ModItems.DEBUG_STICK){
                worldIn.setBlockState(pos,state.withProperty(STATE,(state.getValue(STATE)==1?3:state.getValue(STATE)-1)));
            }
            else{
                if(state.getValue(STATE)==2) {
                    worldIn.setBlockState(pos, state.withProperty(STATE, 1));
                    //EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.PU_GONG_YING_ZI, 1));
                    //worldIn.spawnEntity(entityitem);
                    //worldIn.playSound(null,pos, SoundsHandler.PICK, SoundCategory.BLOCKS,5,1);
                    worldIn.scheduleUpdate(pos, this, 200);
                }
            }
        }
        return true;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        //todo:add feng judgement
        if(state.getValue(STATE)==3){
            worldIn.setBlockState(pos,state.withProperty(STATE,2));
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        worldIn.setBlockState(pos,state.withProperty(STATE,3));
    }
}
