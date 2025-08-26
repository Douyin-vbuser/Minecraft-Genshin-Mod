package com.vbuser.particulate;

import com.vbuser.particulate.command.CmdB;
import com.vbuser.particulate.command.CmdP;
import com.vbuser.particulate.event.ClearExpBar;
import com.vbuser.particulate.network.block.PacketBlock;
import com.vbuser.particulate.network.block.PacketTCB;
import com.vbuser.particulate.network.block.PacketTSB;
import com.vbuser.particulate.network.particle.PacketCommon;
import com.vbuser.particulate.network.particle.PacketSimple;
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

/**
 * Particulate 类是Minecraft模组的主类<br>
 * 负责初始化所有组件，包括网络通信、事件处理器、命令和渲染系统<br>
 * 使用@Mod注解标记为Forge模组的主类
 */
@Mod(modid = "particulate", name = "Custom Renderer Mod", version = "alpha 1.3.12")
public class Particulate {

    /**
     * 网络通信包装器<br>
     * 用于在客户端和服务器之间发送自定义数据包
     */
    public static SimpleNetworkWrapper networkWrapper;

    /**
     * 模组初始化方法<br>
     * 在Forge模组加载时调用，注册所有必要的组件
     * @param event 初始化事件对象
     */
    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        // 初始化粒子系统
        ParticleUtil.init();
        // 注册网络通信
        registerNetwork();

        // 初始化着色器管理器
        ShaderManager.init();
        // 注册事件处理器
        MinecraftForge.EVENT_BUS.register(new RenderEventHandler());
        MinecraftForge.EVENT_BUS.register(new BlockRenderer());
        MinecraftForge.EVENT_BUS.register(new ClearExpBar());
    }

    /**
     * 注册网络通信通道和数据包处理器<br>
     * 为客户端和服务器之间的通信设置消息处理器
     */
    public static void registerNetwork() {
        // 创建简单的网络通道
        networkWrapper = NetworkRegistry.INSTANCE.newSimpleChannel("ParticulateChannel");
        // 注册各种数据包及其处理器
        networkWrapper.registerMessage(PacketCommon.Handle.class, PacketCommon.class, 300, Side.CLIENT);
        networkWrapper.registerMessage(PacketBlock.Handler.class, PacketBlock.class, 301, Side.CLIENT);
        networkWrapper.registerMessage(PacketTCB.Handler.class, PacketTCB.class, 302, Side.CLIENT);
        networkWrapper.registerMessage(PacketTSB.Handler.class, PacketTSB.class, 303, Side.SERVER);
        networkWrapper.registerMessage(PacketSimple.Handler.class, PacketSimple.class, 304, Side.CLIENT);
    }

    /**
     * 服务器初始化方法<br>
     * 在服务器启动时调用，注册自定义命令
     * @param event 服务器启动事件对象
     */
    @Mod.EventHandler
    public void serverInit(FMLServerStartingEvent event) {
        // 注册粒子命令
        event.registerServerCommand(new CmdP());
        // 注册方块命令
        event.registerServerCommand(new CmdB());
    }

    /**
     * 向指定玩家发送方块渲染数据包<br>
     * 这是模组提供的API方法，可用于在其他地方触发方块渲染
     * @param player 目标玩家
     * @param pos 方块位置
     * @param block 方块对象
     * @param meta 方块元数据
     */
    public static void renderBlock(EntityPlayerMP player, BlockPos pos, Block block, int meta) {
        networkWrapper.sendTo(new PacketBlock(block, meta, pos), player);
    }
}