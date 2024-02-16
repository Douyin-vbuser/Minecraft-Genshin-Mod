package com.vbuser.genshin.blocks;

import com.vbuser.genshin.util.handler.SoundsHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("all")
public class TianTianHua extends FlowerBase {

    public static final PropertyBool PICKED = PropertyBool.create("picked");

    public TianTianHua(String name, Material material){
        super(name,material);
        setDefaultState(this.blockState.getBaseState().withProperty(PICKED,false));
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this,PICKED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PICKED,meta==1);
    }

    @Override
    public int getMetaFromState(IBlockState state){return state.getValue(PICKED)?1:0;}

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if (worldIn.isRemote)
        {
            return true;
        }
        else {
            worldIn.setBlockState(pos, state.withProperty(PICKED, true));
            if (!state.getValue(PICKED)) {
                //EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Item.getItemFromBlock(ModBlocks.TIAN_TIAN_HUA), 1));
                //worldIn.spawnEntity(entityitem);
                worldIn.playSound(null,pos, SoundsHandler.PICK, SoundCategory.BLOCKS,5,1);
                worldIn.scheduleUpdate(pos, this, 200);
            }
            return true;
        }
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        worldIn.setBlockState(pos,state.withProperty(PICKED,false));
    }
}
