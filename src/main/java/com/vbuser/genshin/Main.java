package com.vbuser.genshin;

import com.vbuser.genshin.proxy.CommonProxy;
import com.vbuser.genshin.tabs.TabBase;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;

import java.util.List;
import java.util.ArrayList;

@SuppressWarnings("all")
@Mod(modid = "genshin", name = "Minecraft Genshin Impact", version = "basic 1.0.0(refactored_version)")
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

    static {
        TABS.add(SHENG_YI_WU);
        TABS.add(WU_QI);
        TABS.add(SHI_WU);
    }
}
