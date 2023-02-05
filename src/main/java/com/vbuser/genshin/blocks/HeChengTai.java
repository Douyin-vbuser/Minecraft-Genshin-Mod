package com.vbuser.genshin.blocks;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.inventories.ContainerHeChengTai;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IInteractionObject;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SuppressWarnings("all")
public class HeChengTai extends BlockBase {
    public HeChengTai(String name,Material material){
        super(name,material);
        setCreativeTab(Main.JIANCAI_TAB);
    }

    //提醒Minecraft渲染相邻方块的面
    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT;
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

    //右击事件
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            playerIn.displayGui(new InterfaceCraftingTable(worldIn, pos));
            //直接调用工作台的游戏逻辑
        }
        return true;
    }

    //因为Minecraft限制了工作台容器的调用方块 需要抄下工作台的这个接口并重导向至经过修改的容器类
    public static class InterfaceCraftingTable implements IInteractionObject
    {
        private final World world;
        private final BlockPos position;

        public InterfaceCraftingTable(World worldIn, BlockPos pos)
        {
            this.world = worldIn;
            this.position = pos;
        }

        public String getName()
        {
            return "crafting_table";
        }

        public boolean hasCustomName()
        {
            return false;
        }

        public ITextComponent getDisplayName()
        {
            return new TextComponentTranslation(Blocks.CRAFTING_TABLE.getUnlocalizedName() + ".name", new Object[0]);
        }

        public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn)
        {
            return new ContainerHeChengTai(playerInventory, this.world, this.position);
        }             //该接口仅该方法与net.minecraft.block.BlockWorkbench不同,其余代码一致

        public String getGuiID()
        {
            return "minecraft:crafting_table";
        }
    }
}
