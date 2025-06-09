package com.vbuser.genshin;

import com.vbuser.ime.IMEController;
import net.minecraft.client.Minecraft;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@SuppressWarnings("unused")
@Mod(modid = "genshin", name = "Minecraft Genshin Mod", version = "basic 1.14")
public class Main {

    /**
     * Register Event:
     */

    @Mod.Instance
    public static Main instance;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        System.out.println("[IME] Loading IME Native Library.");
        IMEController.load();
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
    }

    @SubscribeEvent
    public static void onEvent(final RegistryEvent.Register<Biome> event) {
    }

    /**
     * IME Event:
     */
    boolean ime = false;

    @SubscribeEvent
    public void ime(TickEvent.ClientTickEvent event) {
        if (!ime && Minecraft.getMinecraft().player != null) {
            ime = true;
            IMEController.toggleIME(true);
        } else if (ime && Minecraft.getMinecraft().player == null) {
            ime = false;
            IMEController.toggleIME(false);
        }
    }

}
