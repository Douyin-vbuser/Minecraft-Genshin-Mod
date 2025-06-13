package com.vbuser.particulate;

import com.vbuser.particulate.network.particle.PacketLeave;
import com.vbuser.particulate.util.ParticleUtil;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = "particulate", name = "Custom Renderer Mod", version = "basic 0.0.1")
public class Particulate {

    public static SimpleNetworkWrapper networkWrapper;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        ParticleUtil.init();
        registerNetwork();
    }

    public static void registerNetwork() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("SSLChannel");
        networkWrapper.registerMessage(PacketLeave.Handle.class, PacketLeave.class, 300, Side.CLIENT);
    }


    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new com.vbuser.particulate.command.CmdP());
    }

}
