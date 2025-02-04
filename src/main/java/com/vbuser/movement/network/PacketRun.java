package com.vbuser.movement.network;

import com.vbuser.movement.event.PlayerMovement;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class PacketRun implements IMessage {

    private String player;

    public PacketRun() {
    }

    public PacketRun(UUID player) {
        this.player = player.toString();
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.player = buffer.readString(114);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(player);
    }

    public static class Handler implements IMessageHandler<PacketRun, IMessage> {

        @Override
        public IMessage onMessage(PacketRun message, MessageContext ctx) {
            UUID uuid = UUID.fromString(message.player);
            PlayerMovement.sprintMap.put(uuid, !PlayerMovement.sprintMap.get(uuid));
            return null;
        }
    }
}
