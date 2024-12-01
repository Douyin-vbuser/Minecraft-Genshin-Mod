package com.vbuser.genshin.blocks;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.entity.element.bing.Bing;
import com.vbuser.genshin.entity.element.huo.Huo;
import com.vbuser.genshin.entity.element.shui.Shui;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.potion.ModPotions;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Random;

public class HuoJu extends BlockBase {

    public static final PropertyBool FIRED = PropertyBool.create("fired");

    public HuoJu(String name, Material material) {
        super(name, material);
        setCreativeTab(Main.JIAN_CAI);
        setDefaultState(this.blockState.getBaseState().withProperty(FIRED, true));
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && playerIn.getHeldItemMainhand().getItem() == ModItems.DEBUG_STICK) {
            worldIn.setBlockState(pos, state.withProperty(FIRED, !state.getValue(FIRED)));
            worldIn.scheduleUpdate(pos, this, 10);
        }
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos, this, 10);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (state.getValue(FIRED)) {
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
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FIRED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FIRED, meta == 0);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FIRED) ? 0 : 1;
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
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn) {
        if (entityIn instanceof Huo) {
            worldIn.setBlockState(pos, state.withProperty(FIRED, true));
        }
        //TODO:with Entity instanceof Feng
        if (entityIn instanceof Shui || entityIn instanceof Bing) {
            worldIn.setBlockState(pos, state.withProperty(FIRED, false));
        }
        if (state.getValue(FIRED)) {
            if (entityIn instanceof EntityPlayer) {
                //todo:damage player's character
                ((EntityPlayer) entityIn).addPotionEffect(new PotionEffect(ModPotions.HUO, 20, 1));
            }
        }
    }
}
