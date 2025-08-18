package com.vbuser.particulate;

import com.vbuser.particulate.command.CmdB;
import com.vbuser.particulate.command.CmdP;
import com.vbuser.particulate.event.ClearExpBar;
import com.vbuser.particulate.network.block.PacketBlock;
import com.vbuser.particulate.network.block.PacketTCB;
import com.vbuser.particulate.network.block.PacketTSB;
import com.vbuser.particulate.network.particle.PacketLeave;
import com.vbuser.particulate.render.BlockRenderer;
import com.vbuser.particulate.shader.RenderEventHandler;
import com.vbuser.particulate.shader.ShaderManager;
import com.vbuser.particulate.util.ParticleUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
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

        ShaderManager.init();
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new BlockRenderer());
        MinecraftForge.EVENT_BUS.register(new ClearExpBar());
    }

    public static void registerNetwork() {
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("ParticulateChannel");
        networkWrapper.registerMessage(PacketLeave.Handle.class, PacketLeave.class, 300, Side.CLIENT);
        networkWrapper.registerMessage(PacketBlock.Handler.class, PacketBlock.class, 301, Side.CLIENT);
        networkWrapper.registerMessage(PacketTCB.Handler.class, PacketTCB.class, 302, Side.CLIENT);
        networkWrapper.registerMessage(PacketTSB.Handler.class, PacketTSB.class, 303, Side.SERVER);
    }


    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new CmdP());
        event.registerServerCommand(new CmdB());
    }

    //APIs:
    public static void renderBlock(EntityPlayerMP player, BlockPos pos, Block block, int meta) {
        networkWrapper.sendTo(new PacketBlock(block, meta, pos), player);
    }

}
