package com.vbuser.genshin.blocks;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.gui.GuiCookMain;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.potion.ModPotions;
import com.vbuser.genshin.util.ModDamageSource;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;

/**烹饪系统的实现参考了Titouan-Schotte的MMoCook(未声明开源协议)
 *该项目中的代码思路与国内主流开发教程与架构(IDF)存在一定差异
 */
@SuppressWarnings("all")
public class GouHuo extends BlockBase {

    public static PropertyBool FIRED = PropertyBool.create("fired");

    public GouHuo(String name, Material material) {
        super(name,material);
        setCreativeTab(Main.JIANCAI_TAB);
        setDefaultState(this.blockState.getBaseState().withProperty(FIRED,true));
    }

    //方块属性
    @Override
    protected BlockStateContainer createBlockState(){
        return new BlockStateContainer(this,FIRED);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FIRED,meta==0);
    }

    @Override
    public int getMetaFromState(IBlockState state){return state.getValue(FIRED)?0:1;}

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

    //使方块可被玩家通过
    @Override
    @Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }

    @Override
    public void onEntityCollidedWithBlock(World worldIn, BlockPos pos, IBlockState state, Entity entityIn){
        //TODO:与火(含风染)元素实现实体碰撞点燃
        //TODO:与水(含风染) 风(纯色) 冰(含风染)元素实现实体碰撞熄灭，并将(纯色)风元素染色
        if(state.getValue(FIRED)) {
            if (entityIn instanceof EntityPlayer) {
                //TODO:与玩家碰撞造成燃烧伤害(需等精通系统完成)
                entityIn.attackEntityFrom(ModDamageSource.HUO,1);
                ((EntityPlayer) entityIn).addPotionEffect(new PotionEffect(ModPotions.HUO, 20, 1));
            }
        }
    }

    //右键事件
    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if(!worldIn.isRemote) {
            if (playerIn.getHeldItemMainhand().getItem() == ModItems.DEBUG_STICK) {
                worldIn.setBlockState(pos, state.withProperty(FIRED, !state.getValue(FIRED)));
            }
            else {
                if(state.getValue(FIRED)) {
                    Minecraft.getMinecraft().displayGuiScreen(new GuiCookMain(Minecraft.getMinecraft()));
                }
                else{
                    Minecraft mc = Minecraft.getMinecraft();
                    EntityPlayerSP player = mc.player;
                    player.sendMessage(new TextComponentString(I18n.format("fire_first")));
                }
            }
        }
        return true;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if(worldIn.isRaining() && worldIn.canSeeSky(pos)){
            worldIn.setBlockState(pos,state.withProperty(FIRED,false));
        }
    }
}