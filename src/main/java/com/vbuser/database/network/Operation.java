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

public class Operation implements IMessage {
    double mark;
    String command;
    UUID player;

    public Operation() {
    }

    public Operation(double mark, String command, UUID player) {
        this.mark = mark;
        this.command = command;
        this.player = player;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        mark = buffer.readDouble();
        command = buffer.readString(514);
        player = UUID.fromString(buffer.readString(64));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeDouble(mark);
        buffer.writeString(command);
        buffer.writeString(player.toString());
    }

    public static class Handle implements IMessageHandler<Operation, IMessage> {
        @Override
        public IMessage onMessage(Operation message, MessageContext ctx) {
            if (ctx.side.isServer()) {
                Console.dataBase = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(), "genshin_data");
                String result = Console.executeCommand(message.command);
                EntityPlayerMP player = (EntityPlayerMP) ctx.getServerHandler().player.world.getPlayerEntityByUUID(message.player);
                DataBase.network.sendTo(new Feedback(message.mark, result), player);
            }
            return null;
        }
    }
}
