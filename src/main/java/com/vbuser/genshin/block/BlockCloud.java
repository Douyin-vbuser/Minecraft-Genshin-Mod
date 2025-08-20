package com.vbuser.genshin.block;

import com.vbuser.genshin.math.PerlinNoise;
import com.vbuser.genshin.util.IHasModel;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("all")
public class BlockCloud extends BlockBase implements IHasModel {
    public static final PropertyInteger DENSITY = PropertyInteger.create("density", 0, 15);

    public BlockCloud(String name, Material material) {
        super(name, material);
        this.setDefaultState(this.blockState.getBaseState().withProperty(DENSITY, 8));
        this.setSoundType(SoundType.SNOW);
        this.setHardness(0.1F);
        this.setLightOpacity(0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, DENSITY);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(DENSITY, meta);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(DENSITY);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.TRANSLUCENT;
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
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        return 0;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess, BlockPos pos, EnumFacing side) {
        IBlockState adjacentState = blockAccess.getBlockState(pos.offset(side));
        int thisDensity = blockState.getValue(DENSITY);

        if (thisDensity <= 1 && adjacentState.getBlock().isAir(adjacentState, blockAccess, pos.offset(side))) {
            return true;
        }

        if (adjacentState.getBlock() instanceof BlockCloud) {
            int adjacentDensity = adjacentState.getValue(DENSITY);

            if (thisDensity == adjacentDensity) {
                return false;
            }
        }

        return super.shouldSideBeRendered(blockState, blockAccess, pos, side);
    }

    public static float getCloudDensityNoise(int x, int y, int z, long seed, int minY, int maxY) {
        Random rand = new Random(seed);

        float noise = PerlinNoise.noise3d(
                x * 0.1f + rand.nextInt(10000),
                y * 0.05f + rand.nextInt(10000),
                z * 0.1f + rand.nextInt(10000)
        );

        noise = (noise + 1.0f) * 0.5f;

        float baselineHeight = minY * 0.75f + maxY * 0.25f;
        float heightFactor = 1.0f - Math.abs(y - baselineHeight) / (maxY - minY);
        heightFactor = Math.max(0, Math.min(1, heightFactor));

        noise = noise * heightFactor;

        return noise;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public float getAmbientOcclusionLightValue(IBlockState state) {
        return 1.0F;
    }
}