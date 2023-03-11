package com.vbuser.genshin.blocks;

import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.util.handlers.SoundsHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Random;

@SuppressWarnings("all")
public class XiaoDengCao extends FlowerBase {

    public static final PropertyBool PICKED = PropertyBool.create("picked");

    public static final PropertyBool NOISED = PropertyBool.create("noised");

    public XiaoDengCao(String name, Material material){
        super(name,material);
        setDefaultState(this.blockState.getBaseState().withProperty(PICKED,false));
        setLightLevel(0.4f);
    }

    //方块属性
    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this,PICKED,NOISED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(PICKED,meta%2==1).withProperty(NOISED,meta>2);
    }

    @Override
    public int getMetaFromState(IBlockState state){
        return state.getValue(PICKED)?1:0;
    }

    //右键事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if (worldIn.isRemote)
        {
            return true;
        }
        else {
            worldIn.setBlockState(pos, state.withProperty(PICKED, true));
            if (!state.getValue(PICKED)) {
                EntityItem entityitem = new EntityItem(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(Item.getItemFromBlock(ModBlocks.XIAO_DENG_CAO), 1));
                worldIn.spawnEntity(entityitem);
                worldIn.playSound(null,pos, SoundsHandler.PICK, SoundCategory.BLOCKS,5,1);
                worldIn.scheduleUpdate(pos, this, 200);      //延时10秒执行updateTick方法
            }
            return true;
        }
    }

    //实体碰撞事件
    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        if(entityIn instanceof EntityPlayer &!state.getValue(PICKED) &! state.getValue(NOISED)){
            worldIn.playSound(null,pos,SoundsHandler.XDC, SoundCategory.BLOCKS,1,1);
            worldIn.setBlockState(pos,state.withProperty(NOISED,true));
        }
        //热知识：小灯草在玩家路过时会发出电流声
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand){
        worldIn.setBlockState(pos,state.withProperty(PICKED,false).withProperty(NOISED,false));
    }
}
