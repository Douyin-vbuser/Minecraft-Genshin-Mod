package com.vbuser.database;

import com.vbuser.database.network.Feedback;
import com.vbuser.database.network.Operation;
import com.vbuser.database.network.Storage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.Random;

/**
 * 数据库模组主类<br>
 * 负责初始化Minecraft模组并提供数据库命令执行接口
 */
@SuppressWarnings("unused")
@Mod(modid = "database", name = "Database")
public class DataBase {
    // 网络通信通道
    public static SimpleNetworkWrapper network;

    /**
     * Forge预初始化事件处理器<br>
     * 注册网络消息和处理器
     * @param event FML预初始化事件
     */
    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        // 创建网络通道
        network = NetworkRegistry.INSTANCE.newSimpleChannel("database");
        // 注册操作消息（客户端->服务端）
        network.registerMessage(Operation.Handle.class, Operation.class, 200, Side.SERVER);
        // 注册反馈消息（服务端->客户端）
        network.registerMessage(Feedback.Handle.class, Feedback.class, 201, Side.CLIENT);
    }

    /**
     * 执行数据库命令<br>
     * 通过网络发送到服务端执行并等待结果
     * @param command 数据库命令
     * @param mp 执行命令的玩家
     * @return 命令执行结果
     * @throws InterruptedException 如果等待结果时线程被中断
     */
    public static String execute(String command, EntityPlayer mp) throws InterruptedException {
        // 生成唯一标记
        double mark = new Random().nextDouble();
        // 发送操作消息到服务端
        network.sendToServer(new Operation(mark, command, mp.getUniqueID()));
        // 等待结果返回
        while (!Storage.select.containsKey(mark)) {
            Thread.sleep(10);
        }
        // 获取并移除结果
        return Storage.select.remove(mark);
    }
}