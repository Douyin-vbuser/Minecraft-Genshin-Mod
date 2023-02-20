package com.vbuser.genshin.blocks.tileEntity;

import com.vbuser.genshin.blocks.BlockBase;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Objects;

public class BaoXiang extends BlockBase {

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 1, 5);
    //1:普通的宝箱 2:精致的宝箱 3:珍贵的宝箱 4:华丽的宝箱 5:奇馈宝箱

    public BaoXiang(String name, Material material) {
        super(name, material);
        setHardness(1f);
        setDefaultState(blockState.getBaseState().withProperty(LEVEL, 1));
    }

    //方块属性
    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(LEVEL, meta + 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(LEVEL) - 1;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, LEVEL);
    }

    //右击事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote){
            if(playerIn.getHeldItemMainhand().getItem()== ModItems.DEBUG_STICK) {
                playerIn.displayGUIChest(Objects.requireNonNull(this.getLockableContainer(worldIn, pos)));
            }
            if(playerIn.getHeldItemMainhand().getItem()== Item.getItemFromBlock(ModBlocks.BAO_XIANG)){
                worldIn.setBlockState(pos,state.withProperty(LEVEL,state.getValue(LEVEL)%5+1));
            }
            else{
                TileEntity tileentity = worldIn.getTileEntity(pos);

                if (tileentity instanceof IInventory)
                {
                    InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory)tileentity);
                    worldIn.updateComparatorOutputLevel(pos, this);
                }
                worldIn.removeTileEntity(pos);
            }
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(){
        return true;
    }

    @Override
    public TileEntity createTileEntity(World worldIn,IBlockState state){
        return new TileEntityChest();
    }

    @Nullable
    public ILockableContainer getLockableContainer(World worldIn, BlockPos pos)
    {
        return this.getContainer(worldIn, pos);
    }

    @Nullable
    public ILockableContainer getContainer(World worldIn, BlockPos pos)
    {
        TileEntity tileentity = worldIn.getTileEntity(pos);

        if (!(tileentity instanceof TileEntityChest))
        {
            return null;
        }
        else
        {
            ILockableContainer ilockablecontainer = (TileEntityChest)tileentity;

                for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL)
                {
                    BlockPos blockpos = pos.offset(enumfacing);
                    Block block = worldIn.getBlockState(blockpos).getBlock();

                    if (block == this)
                    {
                        TileEntity tileentity1 = worldIn.getTileEntity(blockpos);

                        if (tileentity1 instanceof TileEntityChest)
                        {
                            ilockablecontainer = new InventoryLargeChest("container.chestDouble", ilockablecontainer, (TileEntityChest)tileentity1);
                        }
                    }
                }

                return ilockablecontainer;
        }
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

    //提醒Minecraft渲染相邻方块的面
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }
}