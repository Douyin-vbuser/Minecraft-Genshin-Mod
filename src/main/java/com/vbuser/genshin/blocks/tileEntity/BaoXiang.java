package com.vbuser.genshin.blocks.tileEntity;

import com.vbuser.genshin.init.ModBlocks;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("all")
public class BaoXiang extends BlockContainerBase {

    public static final PropertyInteger LEVEL = PropertyInteger.create("level", 1, 5);
    //1:普通的宝箱 2:精致的宝箱 3:珍贵的宝箱 4:华丽的宝箱 5:奇馈宝箱

    public BaoXiang(String name, Material material) {
        super(name,material);
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
        return new BlockStateContainer(this,LEVEL);
    }



    //右击事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote){
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
                //worldIn.removeTileEntity(pos);
                //worldIn.setBlockToAir(pos);
            }
        }
        return true;
    }

    //方块实体
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityBao();
    }
}