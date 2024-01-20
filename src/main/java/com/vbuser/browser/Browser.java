package com.vbuser.browser;

import com.vbuser.browser.command.CommandCG;
import com.vbuser.browser.gui.ModGuiLoader;
import com.vbuser.browser.network.PacketVideo;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "browser")
public class Browser {

    public static String video="";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        new ModGuiLoader();
    }

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("browser_channel");
        network.registerMessage(PacketVideo.PacketVideoHandler.class, PacketVideo.class,1, Side.CLIENT);
    }

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCG());
    }

    @Mod.Instance
    public static Browser instance;

}
