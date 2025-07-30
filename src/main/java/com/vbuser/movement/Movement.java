package com.vbuser.movement;

import com.vbuser.movement.command.FOVCommand;
import com.vbuser.movement.event.FOVHandler;
import com.vbuser.movement.event.Sprint;
import com.vbuser.movement.network.SprintPacket;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "movement")
public class Movement {

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("MovementChannel");
        network.registerMessage(SprintPacket.Handle.class, SprintPacket.class, 400, Side.SERVER);
        MinecraftForge.EVENT_BUS.register(new Sprint());
        MinecraftForge.EVENT_BUS.register(new FOVHandler());
    }

    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new FOVCommand());
    }

}
