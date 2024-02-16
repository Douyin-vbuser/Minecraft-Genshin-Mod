package com.vbuser.movement.network;

import com.vbuser.movement.Storage;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCameraTrack implements IMessage {

    private int start_x,start_y,start_z,end_x,end_y,end_z,time_last,start_yaw,end_yaw;

    public PacketCameraTrack() {}

    public PacketCameraTrack(int start_x,int start_y,int start_z,int end_x,int end_y,int end_z,int time_last,int start_yaw,int end_yaw){
        this.start_x = start_x;
        this.start_y = start_y;
        this.start_z = start_z;
        this.end_x = end_x;
        this.end_y = end_y;
        this.end_z = end_z;
        this.time_last = time_last;
        this.start_yaw = start_yaw;
        this.end_yaw = end_yaw;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.start_x = buf.readInt();
        this.start_y = buf.readInt();
        this.start_z = buf.readInt();
        this.end_x = buf.readInt();
        this.end_y = buf.readInt();
        this.end_z = buf.readInt();
        this.time_last = buf.readInt();
        this.start_yaw = buf.readInt();
        this.end_yaw = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.start_x);
        buf.writeInt(this.start_y);
        buf.writeInt(this.start_z);
        buf.writeInt(this.end_x);
        buf.writeInt(this.end_y);
        buf.writeInt(this.end_z);
        buf.writeInt(this.time_last);
        buf.writeInt(this.start_yaw);
        buf.writeInt(this.end_yaw);
    }

    public static class Handler implements IMessageHandler<PacketCameraTrack, IMessage> {
        @Override
        public IMessage onMessage(PacketCameraTrack message, MessageContext ctx) {
            Storage.start_x = message.start_x;
            Storage.start_y = message.start_y;
            Storage.start_z = message.start_z;
            Storage.end_x = message.end_x;
            Storage.end_y = message.end_y;
            Storage.end_z = message.end_z;
            Storage.last_time = message.time_last;
            Storage.start_yaw = message.start_yaw;
            Storage.end_yaw = message.end_yaw;
            Storage.pack_received = true;
            return null;
        }
    }
}
