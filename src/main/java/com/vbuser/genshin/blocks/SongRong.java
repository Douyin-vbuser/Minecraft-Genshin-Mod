package com.vbuser.genshin.blocks;

import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.util.handlers.SoundsHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SongRong extends FlowerBase{
    public static final PropertyBool PICKED = PropertyBool.create("picked");

    public SongRong(String name, Material material) {
        super(name, material);
        setDefaultState(this.blockState.getBaseState().withProperty(PICKED,false));
    }

    //方块属性
    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this,PICKED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PICKED,meta==1);
    }

    //右击事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if (!worldIn.isRemote)
        {
            worldIn.setBlockState(pos, state.withProperty(PICKED, true));
            if (!state.getValue(PICKED)) {
                EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Item.getItemFromBlock(ModBlocks.SONG_RONG), 1));
                worldIn.spawnEntity(entityitem);
                worldIn.playSound(null,pos, SoundsHandler.PICK, SoundCategory.BLOCKS,5,1);
            }
        }
        return true;
    }
}
