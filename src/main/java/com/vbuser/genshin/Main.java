package com.vbuser.genshin;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(modid = "genshin", name = "Minecraft Genshin Mod", version = "basic 1.14")
public class Main {

    @Mod.Instance
    public static Main instance;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
    }

    @Mod.EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
    }

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
    }

    @SubscribeEvent
    public static void onEvent(final RegistryEvent.Register<Biome> event){
    }

}
