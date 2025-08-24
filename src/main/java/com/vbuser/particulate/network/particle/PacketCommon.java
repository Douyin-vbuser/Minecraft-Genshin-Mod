package com.vbuser.particulate.network.particle;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketCommon implements IMessage {

    double x, y, z;
    int particleId;

    public PacketCommon() {
    }

    public PacketCommon(double x, double y, double z, int id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleId = id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        particleId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(particleId);
    }

    public static class Handle implements IMessageHandler<PacketCommon, IMessage> {

        @Override
        public IMessage onMessage(PacketCommon message, MessageContext ctx) {
            if (ctx.side.isClient()) {
                World world = Minecraft.getMinecraft().world;
                EnumParticleTypes particle = EnumParticleTypes.values()[message.particleId];
                world.spawnParticle(particle, message.x, message.y, message.z, 0.0, 0.0, 0.0);
            }
            return null;
        }
    }
}
