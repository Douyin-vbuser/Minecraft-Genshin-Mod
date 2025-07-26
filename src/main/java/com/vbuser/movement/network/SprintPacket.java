package com.vbuser.movement.network;

import com.vbuser.movement.event.Sprint;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SprintPacket implements IMessage {

    private boolean sprinting;

    public SprintPacket() {
    }

    public SprintPacket(boolean sprinting) {
        this.sprinting = sprinting;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        sprinting = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeBoolean(sprinting);
    }

    public static class Handle implements IMessageHandler<SprintPacket, IMessage> {

        @Override
        public IMessage onMessage(SprintPacket message, MessageContext ctx) {
            if(ctx.side.isServer()) Sprint.sprintingPlayers
                    .put(ctx.getServerHandler().player, message.sprinting);
            return null;
        }
    }
}
