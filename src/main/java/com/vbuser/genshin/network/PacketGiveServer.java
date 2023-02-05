package com.vbuser.genshin.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketGiveServer implements IMessage {

    private int index;
    private int isGood;
    private int quantity;


    public PacketGiveServer() {
    }

    public PacketGiveServer(int index, int isGood, int quantity) {
        this.index = index;
        this.isGood = isGood;
        this.quantity = quantity;
    }


    @Override
    public void fromBytes(ByteBuf buf) {

        index = buf.readInt();
        isGood = buf.readInt();
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(index);
        buf.writeInt(isGood);
        buf.writeInt(quantity);
    }

    public static class Handler implements IMessageHandler<PacketGiveServer, IMessage> {
        @Override
        public IMessage onMessage(PacketGiveServer message, MessageContext ctx){
            return null;
        }
    }
}