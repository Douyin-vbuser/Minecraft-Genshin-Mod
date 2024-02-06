package com.vbuser.genshin;

import com.vbuser.genshin.proxy.CommonProxy;
import com.vbuser.genshin.tabs.TabBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
@Mod(modid = "genshin", name = "Minecraft Genshin Impact", version = "basic 1.1.45")
public class Main
{

    //Registry Events:

    @Mod.Instance
    public static Main instance;

    @SidedProxy(clientSide =  "com.vbuser.genshin.proxy.ClientProxy", serverSide = "com.vbuser.genshin.proxy.CommonProxy")

    public static CommonProxy proxy;


    //CreativeTabs:

    public static final List<TabBase> TABS = new ArrayList<>();
    public static final TabBase SHENG_YI_WU = new TabBase("sheng_yi_wu", Items.FIREWORKS);
    public static final TabBase WU_QI = new TabBase("wu_qi",Items.BOW);
    public static final TabBase SHI_WU = new TabBase("shi_wu",Items.CHICKEN);
    public static final TabBase PEI_YANG_DAO_JU = new TabBase("pei_yang_dao_ju",Items.BOOK);
    public static final TabBase BAI_SHE = new TabBase("bai_she",Items.BED);
    public static final TabBase ZHENG_GUI_DAO_JU = new TabBase("zheng_gui_dao_ju",Items.DIAMOND);
    public static final TabBase RENG_WU_DAO_JU = new TabBase("reng_wu_dao_ju",Items.DIAMOND_SWORD);
    public static final TabBase ZI_RAN_ZI_YUAN = new TabBase("zi_ran_zi_yuan",Items.IRON_INGOT);
    public static final TabBase XIAO_DAO_JU = new TabBase("xiao_dao_ju",Items.POTATO);
    public static final TabBase JIAN_CAI = new TabBase("jian_cai", Blocks.GRASS);

    static {
        TABS.add(WU_QI);
        TABS.add(SHENG_YI_WU);
        TABS.add(PEI_YANG_DAO_JU);
        TABS.add(SHI_WU);
        TABS.add(ZI_RAN_ZI_YUAN);
        TABS.add(RENG_WU_DAO_JU);
        TABS.add(XIAO_DAO_JU);
        TABS.add(ZHENG_GUI_DAO_JU);
        TABS.add(BAI_SHE);
    }
}
