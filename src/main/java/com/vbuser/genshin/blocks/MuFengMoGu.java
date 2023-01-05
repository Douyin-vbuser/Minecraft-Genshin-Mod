package com.vbuser.genshin.blocks;

import com.google.common.base.Predicate;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.util.handlers.SoundsHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class MuFengMoGu extends BlockBase {

    public static final PropertyInteger FACING = PropertyInteger.create("facing",1,4);
    public static final PropertyBool PICKED = PropertyBool.create("picked");

    public MuFengMoGu(String name, Material material){
        super(name,material);
    }

    //方块属性
    @Override
    public IBlockState getStateFromMeta(int meta){
        return this.getDefaultState().withProperty(FACING,(((meta-meta%2)/2)+1)).withProperty(PICKED,false);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return (state.getValue(FACING)-1)*2+(state.getValue(PICKED)?1:0);
    }

    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this,FACING,PICKED);
    }

    //使玩家可以通过方块
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    //提醒Minecraft渲染相邻方块的面
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    //通过下面几个方法实现不完整方块
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

    //右击事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(playerIn.getHeldItemMainhand().getItem()== ModItems.DEBUG_STICK){
            worldIn.setBlockState(pos,state.withProperty(FACING,state.getValue(FACING)%4+1));   //用于地图搭建时调整其朝向
        }
        else {
            if (!worldIn.isRemote) {
                worldIn.setBlockState(pos, state.withProperty(PICKED, true));
                if (!state.getValue(PICKED)) {
                    EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Item.getItemFromBlock(ModBlocks.MU_FENG_MO_GU), 1));
                    worldIn.spawnEntity(entityitem);
                    worldIn.playSound(null,pos, SoundsHandler.PICK, SoundCategory.BLOCKS,5,1);
                }
            }
        }
        return true;
    }
}
