package com.vbuser.genshin.blocks;

import com.vbuser.genshin.entity.organism.XianLing;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
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
import java.util.Random;

public class XianLingZhiTing extends BlockBase {

    public static final PropertyBool BACKED = PropertyBool.create("backed");

    public XianLingZhiTing(String name, Material material) {
        super(name, material);
        setDefaultState(this.blockState.getBaseState().withProperty(BACKED, false));
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof XianLing) {
            worldIn.setBlockState(pos, state.withProperty(BACKED, true));
            worldIn.scheduleUpdate(pos, this, 10);
            entityIn.setDead();
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && playerIn.getHeldItemMainhand().getItem() == ModItems.DEBUG_STICK) {
            worldIn.setBlockState(pos, state.withProperty(BACKED, !state.getValue(BACKED)));
            worldIn.scheduleUpdate(pos, this, 10);
        }
        return true;
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(BACKED)) {
            int i = 0;
            while (i < 12) {
                if (worldIn.getBlockState(pos.down(i)) == ModBlocks.PRE_STONE.getDefaultState()) {
                    worldIn.setBlockState(pos.down(i), Blocks.REDSTONE_BLOCK.getDefaultState());
                    break;
                } else {
                    i = i + 1;
                }
            }
        } else {
            int i = 0;
            while (i < 12) {
                if (worldIn.getBlockState(pos.down(i)) == Blocks.REDSTONE_BLOCK.getDefaultState()) {
                    worldIn.setBlockState(pos.down(i), ModBlocks.PRE_STONE.getDefaultState());
                    break;
                } else {
                    i = i + 1;
                }
            }
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos) {
        return NULL_AABB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BACKED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(BACKED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BACKED) ? 1 : 0;
    }

}
