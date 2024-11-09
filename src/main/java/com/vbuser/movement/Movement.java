package com.vbuser.movement;

import com.vbuser.movement.command.CommandCameraTrack;
import com.vbuser.movement.entity.EntityInit;
import com.vbuser.movement.entity.FakePlayer;
import com.vbuser.movement.entity.render.FakePlayerRender;
import com.vbuser.movement.event.CameraTrack;
import com.vbuser.movement.event.FOVHandler;
import com.vbuser.movement.event.PlayerMovement;
import com.vbuser.movement.network.TS_TN;
import com.vbuser.movement.network.PacketCameraTrack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import software.bernie.geckolib3.GeckoLib;

@Mod(modid = "movement",version = "alpha 6.1.3")
public class Movement {

    @Mod.Instance
    public static Movement instance;

    public Movement(){
        GeckoLib.initialize();
    }

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        EntityInit.registerEntities();
        RenderingRegistry.registerEntityRenderingHandler(FakePlayer.class, FakePlayerRender::new);

        network = NetworkRegistry.INSTANCE.newSimpleChannel("movement_channel");
        network.registerMessage(PacketCameraTrack.Handler.class, PacketCameraTrack.class,1, Side.CLIENT);
        network.registerMessage(TS_TN.Handler.class, TS_TN.class,2,Side.SERVER);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new CameraTrack());
        MinecraftForge.EVENT_BUS.register(new PlayerMovement());
        MinecraftForge.EVENT_BUS.register(new FOVHandler());
    }

    @Mod.EventHandler
    public void onServerInit(FMLServerStartingEvent event){
        event.registerServerCommand(new CommandCameraTrack());
    }
}
