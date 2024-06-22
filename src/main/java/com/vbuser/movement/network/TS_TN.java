package com.vbuser.movement.network;

import com.vbuser.movement.Storage_s;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class TS_TN implements IMessage {

    private String uuid;
    private boolean option;

    public TS_TN(){}

    public TS_TN(UUID uuid,boolean option){
        this.uuid = uuid.toString();
        this.option = option;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        this.uuid = packetBuffer.readString(114);
        this.option = packetBuffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeString(uuid);
        packetBuffer.writeBoolean(option);
    }

    public static class Handler implements IMessageHandler<TS_TN,IMessage>{

        @Override
        public IMessage onMessage(TS_TN message, MessageContext ctx) {
            if(ctx.getServerHandler().player.getEntityWorld().getPlayerEntityByUUID(UUID.fromString(message.uuid))!=null) {
                Storage_s.normal.put(ctx.getServerHandler().player.getEntityWorld().getPlayerEntityByUUID(UUID.fromString(message.uuid)), message.option);
            }
            return null;
        }
    }
}
