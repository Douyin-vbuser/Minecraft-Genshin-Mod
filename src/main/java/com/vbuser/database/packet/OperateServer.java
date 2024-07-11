package com.vbuser.database.packet;

import com.vbuser.database.operate.Console;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;

public class OperateServer implements IMessage {

    private String command;

    public OperateServer(){}

    public OperateServer(String command){
        this.command = command;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        command = buffer.readString(1145);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(command);
    }

    public static class Handler implements IMessageHandler<OperateServer, IMessage> {
        @Override
        public IMessage onMessage(OperateServer message, MessageContext ctx) {
            Console.setDataBase(new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_data"));
            try {
                System.out.println("[] executing command:"+message.command);
                Console.executeCommand(message.command);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return null;
        }
    }
}
