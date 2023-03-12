package com.vbuser.genshin.blocks.tileEntity;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Random;

//@SuppressWarnings("all")
public class BaoXiang extends BlockContainerBase {

    public static final PropertyBool OPENED = PropertyBool.create("opened");

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 1, 5);
    //1:普通的宝箱 2:精致的宝箱 3:珍贵的宝箱 4:华丽的宝箱 5:奇馈宝箱

    public BaoXiang(String name, Material material) {
        super(name,material);
        setHardness(1f);
        setCreativeTab(Main.ZHENGGUIWUPING_TAB);
        setDefaultState(blockState.getBaseState().withProperty(LEVEL, 1).withProperty(OPENED,false));
    }

    //方块属性
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, (meta-meta%2)/2+1).withProperty(OPENED,meta%2==1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return (state.getValue(LEVEL) - 1)*2+(state.getValue(OPENED)?1:0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this,LEVEL,OPENED);
    }

    //右击事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote){
            if(playerIn.getHeldItemMainhand().getItem()== Item.getItemFromBlock(ModBlocks.BAO_XIANG)){
                TileEntity tileentity = worldIn.getTileEntity(pos);
                ILockableContainer ilockablecontainer = (TileEntityBao)tileentity;
                playerIn.displayGUIChest(ilockablecontainer);
            }
           else{
                if(playerIn.getHeldItemMainhand().getItem()== Items.STICK){
                    int a = state.getValue(LEVEL);
                    worldIn.setBlockState(pos,state.withProperty(LEVEL,a%5+1));
                }
                else{
                    TileEntity tileentity = worldIn.getTileEntity(pos);
                    InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
                    worldIn.scheduleUpdate(pos, this, 30);      //延时1.5秒执行updateTick方法
                    worldIn.setBlockState(pos,state.withProperty(OPENED,true));}
            }
        }
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if(state.getValue(OPENED)) {
            worldIn.setBlockToAir(pos);
            worldIn.removeTileEntity(pos);
        }
    }

    //方块实体
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBao();
    }

    @Override
    public boolean hasTileEntity(){
        return true;
    }

    @Override
    public TileEntity createTileEntity(World worldIn,IBlockState state){
        return new TileEntityBao();
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
}