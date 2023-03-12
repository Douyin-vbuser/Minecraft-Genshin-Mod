package com.vbuser.genshin.blocks.tileEntity;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.blocks.BlockBase;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.util.handlers.SoundsHandler;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

@SuppressWarnings("all")
public class ChuanSongMaoDian extends BlockBase implements ITileEntityProvider {

    public static final PropertyInteger ACTIVE = PropertyInteger.create("active",1,3);

    public ChuanSongMaoDian(String name, Material material){
        super(name,material);
        setCreativeTab(Main.JIANCAI_TAB);
        setDefaultState(this.blockState.getBaseState().withProperty(ACTIVE,1));
    }

    //方块属性
    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this,ACTIVE);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(ACTIVE,meta+1);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(ACTIVE)-1;
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

    //规定由TileEntity决定模型
    @Override
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    //右键事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if (!worldIn.isRemote)
        {
            if(playerIn.getHeldItemMainhand().getItem()==ModItems.DEBUG_STICK){
                worldIn.setBlockState(pos,state.withProperty(ACTIVE,state.getValue(ACTIVE)%3+1));
            }
            else{
                if(state.getValue(ACTIVE)==1){
                    worldIn.setBlockState(pos,state.withProperty(ACTIVE,2));
                    worldIn.scheduleUpdate(pos, this, 45);      //延时2.25秒(actived动画长2.08秒)执行updateTick方法
                    worldIn.playSound(null, pos, SoundsHandler.PICK, SoundCategory.BLOCKS, 5, 1);
                }
            }
        }

        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        worldIn.setBlockState(pos, state.withProperty(ACTIVE, 3));
    }

    //方块实体
    @Override
    public boolean hasTileEntity(){
        return true;
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TileEntityChuan();
    }

    @Override
    public TileEntity createTileEntity(World worldIn,IBlockState state){
        return new TileEntityChuan();
    }
}
