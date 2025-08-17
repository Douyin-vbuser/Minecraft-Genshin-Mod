package com.vbuser.database.network;

import com.vbuser.database.DataBase;
import com.vbuser.database.operate.Console;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;
import java.util.UUID;

/**
 * 数据库操作消息<br>
 * 用于将客户端命令发送到服务端执行
 */
public class Operation implements IMessage {
    double mark;        // 消息唯一标识
    String command;     // 数据库命令
    UUID player;        // 发起命令的玩家UUID

    public Operation() {
    }

    /**
     * 构造函数
     * @param mark 消息标识
     * @param command 数据库命令
     * @param player 玩家UUID
     */
    public Operation(double mark, String command, UUID player) {
        this.mark = mark;
        this.command = command;
        this.player = player;
    }

    /**
     * 从字节缓冲区解码消息<br>
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        mark = buffer.readDouble();
        command = buffer.readString(514);
        player = UUID.fromString(buffer.readString(64));
    }

    /**
     * 将消息编码到字节缓冲区<br>
     */
    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeDouble(mark);
        buffer.writeString(command);
        buffer.writeString(player.toString());
    }

    public static class Handle implements IMessageHandler<Operation, IMessage> {
        /**
         * 处理接收到的操作消息<br>
         * 执行命令并将结果发送回客户端
         */
        @Override
        public IMessage onMessage(Operation message, MessageContext ctx) {
            // 仅在服务端处理
            if (ctx.side.isServer()) {
                // 设置数据库目录
                Console.dataBase = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(), "genshin_data");
                // 执行命令
                String result = Console.executeCommand(message.command);
                // 获取目标玩家
                EntityPlayerMP player = (EntityPlayerMP) ctx.getServerHandler().player.world.getPlayerEntityByUUID(message.player);
                // 发送结果反馈
                DataBase.network.sendTo(new Feedback(message.mark, result), player);
            }
            return null;
        }
    }
}