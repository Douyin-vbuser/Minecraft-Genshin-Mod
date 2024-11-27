package com.vbuser.genshin;

import com.vbuser.genshin.command.CommandChar;
import com.vbuser.genshin.command.CommandSetBiome;
import com.vbuser.genshin.command.CommandTask;
import com.vbuser.genshin.command.CommandTpDim;
import com.vbuser.genshin.event.AttackState;
import com.vbuser.genshin.event.CharacterChoice;
import com.vbuser.genshin.event.Task;
import com.vbuser.genshin.init.EntityInit;
import com.vbuser.genshin.init.InitBiome;
import com.vbuser.genshin.init.InitDimension;
import com.vbuser.genshin.init.KeyboardManager;
import com.vbuser.genshin.proxy.CommonProxy;
import com.vbuser.genshin.tabs.TabBase;
import com.vbuser.genshin.util.handler.RegistryRenderer;
import com.vbuser.genshin.util.handler.SoundsHandler;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.ArrayList;
import java.util.List;

import static com.vbuser.genshin.init.InitBiome.BIOME_TEST;

@SuppressWarnings("all")
@Mod(modid = "genshin", name = "Minecraft Genshin Impact", version = "basic 1.1.45")
public class Main
{

    //Registry Events:

    @Mod.Instance
    public static Main instance;

    @SidedProxy(clientSide =  "com.vbuser.genshin.proxy.ClientProxy", serverSide = "com.vbuser.genshin.proxy.CommonProxy")

    public static CommonProxy proxy;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event){
        SoundsHandler.registerSounds();
        KeyboardManager.init();
        MinecraftForge.EVENT_BUS.register(new CharacterChoice());
        MinecraftForge.EVENT_BUS.register(new AttackState());
        MinecraftForge.EVENT_BUS.register(new Task());
        AttackState.Character.init();
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event){
        EntityInit.registerEntities();
        RegistryRenderer.registry();
        InitBiome.registerBiomes();
        InitDimension.registerDimensions();
    }

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandTpDim());
        event.registerServerCommand(new CommandChar());
        event.registerServerCommand(new CommandTask());
        event.registerServerCommand(new CommandSetBiome());
    }

    @SubscribeEvent
    public static void onEvent(final RegistryEvent.Register<Biome> event){
        final IForgeRegistry<Biome> registry = event.getRegistry();
        registry.register(BIOME_TEST.setRegistryName("genshin","biome_test"));
    }

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
