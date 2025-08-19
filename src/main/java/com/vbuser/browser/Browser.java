package com.vbuser.browser;

import com.vbuser.browser.command.CommandCG;
import com.vbuser.browser.command.CommandWeb;
import com.vbuser.browser.gui.ModGuiLoader;
import com.vbuser.browser.network.PacketVideo;
import com.vbuser.browser.network.PacketWebPage;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@Mod(modid = "browser", dependencies = "required-after:mcef@[1.0,2.0);after:opencomputers;after:computercraft;", version = "release 1.0.8")
public class Browser {

    @SideOnly(Side.CLIENT)
    public static String video = "";

    @SideOnly(Side.CLIENT)
    public static String path;

    public static boolean isCG;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        new ModGuiLoader();
    }

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("browser_channel");
        network.registerMessage(PacketVideo.PacketVideoHandler.class, PacketVideo.class, 500, Side.CLIENT);
        network.registerMessage(PacketWebPage.PacketWebPageHandler.class, PacketWebPage.class, 501, Side.CLIENT);
    }

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandCG());
        event.registerServerCommand(new CommandWeb());
    }

    @Mod.Instance("browser")
    public static Browser instance;

}
