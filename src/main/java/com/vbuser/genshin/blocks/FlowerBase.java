package com.vbuser.genshin.blocks;

import com.vbuser.database.DataBase;
import com.vbuser.genshin.Main;
import com.vbuser.genshin.init.ModBlocks;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.util.IHasModel;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
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

@SuppressWarnings("all")
public class FlowerBase extends Block implements IHasModel {

    public static PropertyInteger PICKED = PropertyInteger.create("picked", 0, 1);

    private String item;

    public FlowerBase(String name, String item) {
        super(Material.GRASS);
        setUnlocalizedName(name);
        setRegistryName(name);
        setCreativeTab(Main.JIAN_CAI);
        setHardness(0f);
        setResistance(0f);
        setSoundType(SoundType.PLANT);
        this.item = "genshin:" + item;

        setDefaultState(getDefaultState().withProperty(PICKED, 1));

        ModBlocks.BLOCKS.add(this);
        ModItems.ITEMS.add(new ItemBlock(this).setRegistryName(this.getRegistryName()));
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(Item.getItemFromBlock(this), 0, "inventory");
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
        return new BlockStateContainer(this, PICKED);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 1 - state.getValue(PICKED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(PICKED, 1 - meta);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos, this, 1000);
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        int range = 30;
        int playerCount = worldIn.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(-range, -range, -range), pos.add(range, range, range))).size();
        if (playerCount == 0 && worldIn.getBlockState(pos).getValue(PICKED) == 0) {
            worldIn.setBlockState(pos, getDefaultState().withProperty(PICKED, 1));
        }
        worldIn.scheduleUpdate(pos, this, 1000);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote) {
            if (state.getValue(PICKED) == 1) {
                DataBase.addItem(Item.getByNameOrId(item), 1, playerIn);
                worldIn.setBlockState(pos, state.withProperty(PICKED, 0));
            }
        }
        return true;
    }
}
