package com.vbuser.genshin.blocks;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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

public class ZhaDan extends BlockBase{
    public ZhaDan(String name, Material material) {
        super(name, material);
        setCreativeTab(Main.JIAN_CAI);
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
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        //todo:import element entity judgement
        worldIn.createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 0, true);
        worldIn.setBlockToAir(pos);
        int i=0;
        while(i<12){
            if(worldIn.getBlockState(pos.down(i))== ModBlocks.PRE_STONE.getDefaultState()){
                worldIn.setBlockState(pos.down(i), Blocks.REDSTONE_BLOCK.getDefaultState());
                break;
            }
            else{
                i=i+1;
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(playerIn.getHeldItemMainhand().getItem()== ModItems.DEBUG_STICK){
            worldIn.createExplosion(null,pos.getX(),pos.getY(),pos.getZ(),0,true);
            worldIn.setBlockToAir(pos);
            int i=0;
            while(i<12){
                if(worldIn.getBlockState(pos.down(i))== ModBlocks.PRE_STONE.getDefaultState()){
                    worldIn.setBlockState(pos.down(i), Blocks.REDSTONE_BLOCK.getDefaultState());
                    break;
                }
                else{
                    i=i+1;
                }
            }
        }
        return true;
    }
}
