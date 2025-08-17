package com.vbuser.database.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * 网络反馈消息<br>
 * 用于将命令执行结果从服务端发送回客户端
 */
public class Feedback implements IMessage {

    double mark;        // 消息唯一标识
    String result;      // 命令执行结果

    public Feedback() {
    }

    /**
     * 构造函数
     * @param mark 消息标识
     * @param result 执行结果
     */
    public Feedback(double mark, String result) {
        this.mark = mark;
        this.result = result;
    }

    /**
     * 从字节缓冲区解码消息<br>
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.mark = buffer.readDouble();
        this.result = buffer.readString(1145141919);
    }

    /**
     * 将消息编码到字节缓冲区<br>
     */
    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeDouble(mark);
        buffer.writeString(result);
    }

    public static class Handle implements IMessageHandler<Feedback, IMessage> {
        /**
         * 处理接收到的反馈消息<br>
         * 将结果存储到客户端缓存中
         */
        @Override
        public IMessage onMessage(Feedback message, MessageContext ctx) {
            // 仅在客户端处理
            if (ctx.side.isClient()) {
                Storage.select.put(message.mark, message.result);
            }
            return null;
        }
    }
}