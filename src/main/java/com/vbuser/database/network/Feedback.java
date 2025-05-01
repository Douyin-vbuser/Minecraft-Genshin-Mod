package com.vbuser.database.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Feedback implements IMessage {

    double mark;
    String result;

    public Feedback() {
    }

    public Feedback(double mark, String result) {
        this.mark = mark;
        this.result = result;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.mark = buffer.readDouble();
        this.result = buffer.readString(Integer.MAX_VALUE);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeDouble(mark);
        buffer.writeString(result);
    }

    public static class Handle implements IMessageHandler<Feedback, IMessage> {
        @Override
        public IMessage onMessage(Feedback message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                Storage.select.put(message.mark, message.result);
            }
            return null;
        }
    }
}
