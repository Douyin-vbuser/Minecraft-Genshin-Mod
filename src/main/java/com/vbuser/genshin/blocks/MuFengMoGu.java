package com.vbuser.genshin.blocks;

import com.vbuser.genshin.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("all")
public class MuFengMoGu extends BlockBase {

    public static final PropertyInteger FACING = PropertyInteger.create("facing",1,4);
    public static final PropertyBool PICKED = PropertyBool.create("picked");

    public MuFengMoGu(String name, Material material){
        super(name,material);
    }

    @Override
    public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(FACING,(((meta-meta%2)/2)+1)).withProperty(PICKED,meta%2==1);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return (state.getValue(FACING)-1)*2+(state.getValue(PICKED)?1:0);
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this,FACING,PICKED);
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(playerIn.getHeldItemMainhand().getItem()== ModItems.DEBUG_STICK){
            worldIn.setBlockState(pos,state.withProperty(FACING,state.getValue(FACING)%4+1));   //用于地图搭建时调整其朝向
        }
        else {
            if (!worldIn.isRemote) {
                worldIn.setBlockState(pos, state.withProperty(PICKED, true));
                if (!state.getValue(PICKED)) {
                    //EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Item.getItemFromBlock(ModBlocks.MU_FENG_MO_GU), 1));
                    //worldIn.spawnEntity(entityitem);
                    //worldIn.playSound(null,pos, SoundsHandler.PICK, SoundCategory.BLOCKS,5,1);
                    worldIn.scheduleUpdate(pos, this, 200);
                }
            }
        }
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        worldIn.setBlockState(pos,state.withProperty(PICKED,false));
    }
}
