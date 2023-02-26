package com.vbuser.genshin;

import com.vbuser.genshin.blocks.tileEntity.TileEntityChuan;
import com.vbuser.genshin.client.renderer.tile.TileChuanRenderer;
import com.vbuser.genshin.gui.ModGuiLoader;
import com.vbuser.genshin.key.KeyboardManager;
import com.vbuser.genshin.network.PacketGiveServer;
import com.vbuser.genshin.network.PacketInventaireClient;
import com.vbuser.genshin.network.PacketInventaireServer;
import com.vbuser.genshin.proxy.CommonProxy;
import com.vbuser.genshin.tab.*;
import com.vbuser.genshin.util.Reference;
import com.vbuser.genshin.util.handlers.RegistryHandler;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.CoreModManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.spongepowered.asm.mixin.Mixin;
import software.bernie.example.GeckoLibMod;
import software.bernie.example.block.tile.FertilizerTileEntity;
import software.bernie.example.client.renderer.tile.FertilizerTileRenderer;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.resource.ResourceListener;

import java.util.concurrent.FutureTask;

import static software.bernie.geckolib3.GeckoLib.hasInitialized;

@Mod(modid = Reference.Mod_ID,name = Reference.NAME,version = Reference.VERSION)
public class Main {

    @Mod.Instance
    public static Main instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS,serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event){
        RegistryHandler.preInitRegistries(event);
    }

    @Mod.EventHandler
    public static void Init(FMLInitializationEvent event){
        RegistryHandler.initRegistries();
        KeyboardManager.init();
        initialize();
        new ModGuiLoader();
    }

    @Mod.EventHandler
    public static void PostInit(FMLPostInitializationEvent event){}

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        RegistryHandler.serverRegistries(event);
    }

    //geckolib≥ı ºªØ
    public static void initialize() {
        if (!hasInitialized) {
            FMLCommonHandler.callFuture(new FutureTask<>(() -> {
                if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
                    doOnlyOnClient();
                }
            }, null));
        }
        hasInitialized = true;
    }

    @SideOnly(Side.CLIENT)
    private static void doOnlyOnClient() {
        ResourceListener.registerReloadListener();
    }

    @SideOnly(Side.CLIENT)
    @Mod.EventHandler
    public void registerRenderers(FMLPreInitializationEvent event) {
        if (!GeckoLibMod.DISABLE_IN_DEV) {
            ClientRegistry.bindTileEntitySpecialRenderer(TileEntityChuan.class, new TileChuanRenderer());
        }
    }

    /**
     * some CreativeTabs for mod
     * see the translations and notes of the tabs above in package "tab" (Ctrl + click(in IDEA))
     **/

    public static CreativeTabs JIANCAI_TAB = new JianCaiTab();

    public static CreativeTabs SHENGYIWU_TAB = new ShengYiWuTab();

    public static CreativeTabs ZHENGGUIWUPING_TAB = new ZhengGuiWuPingTab();

    public static CreativeTabs SHIWUTAB = new ShiWuTab();

    public static CreativeTabs ZIRANCAILIAO = new ZiRanCaiLiao();

    public static CreativeTabs YANGCHENGCAILIAO = new YangChengCaiLiao();
}
