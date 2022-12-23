package com.vbuser.genshin.blocks;

import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

@SuppressWarnings("all")
public class LuoLuoMei extends FlowerBase {

    public static final PropertyInteger COUNT = PropertyInteger.create("count", 1, 4);

    public LuoLuoMei(String name, Material material) {
        super(name, material);
        setDefaultState(this.blockState.getBaseState().withProperty(COUNT, 4));
    }

    //方块属性
    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, COUNT);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(COUNT, 4 - meta);
    }

    //右击事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (worldIn.isRemote)
        {
            return true;
        }
        else {
            if (state.getValue(COUNT) != 1) {
                EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ModItems.LUOLUOMEI, 1));
                worldIn.spawnEntity(entityitem);
            }
            worldIn.setBlockState(pos, state.withProperty(COUNT, state.getValue(COUNT) == 1 ? 1 : state.getValue(COUNT) - 1));
            return true;
        }
    }
}