package com.vbuser.genshin.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketInventaireServer implements IMessage {

    public PacketInventaireServer() {
    }

    public PacketInventaireServer(int index, int isGood) {

    }


    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketInventaireServer, PacketInventaireClient> {


        @Override
        public PacketInventaireClient onMessage(PacketInventaireServer message, MessageContext ctx) {
            return null;
        }
    }
}