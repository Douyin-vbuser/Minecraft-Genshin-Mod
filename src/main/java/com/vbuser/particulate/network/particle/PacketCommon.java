package com.vbuser.particulate.network.particle;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * PacketCommon 类用于在客户端和服务器之间传输普通粒子效果信息<br>
 * 实现了IMessage接口，用于网络序列化和反序列化<br>
 * 用于同步原版Minecraft粒子效果的生成
 */
public class PacketCommon implements IMessage {

    double x, y, z;  // 粒子生成位置的坐标
    int particleId;  // 粒子类型ID，对应EnumParticleTypes的索引

    /**
     * 无参构造函数，Forge网络框架需要
     */
    public PacketCommon() {
    }

    /**
     * 带参构造函数
     * @param x 粒子生成的x坐标
     * @param y 粒子生成的y坐标
     * @param z 粒子生成的z坐标
     * @param id 粒子类型ID，对应EnumParticleTypes的索引
     */
    public PacketCommon(double x, double y, double z, int id) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.particleId = id;
    }

    /**
     * 从字节缓冲区读取数据，反序列化数据包<br>
     * 读取顺序必须与toBytes方法的写入顺序一致
     * @param buf 字节缓冲区
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readDouble();
        y = buf.readDouble();
        z = buf.readDouble();
        particleId = buf.readInt();
    }

    /**
     * 将数据写入字节缓冲区，序列化数据包<br>
     * 写入顺序必须与fromBytes方法的读取顺序一致
     * @param buf 字节缓冲区
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeDouble(x);
        buf.writeDouble(y);
        buf.writeDouble(z);
        buf.writeInt(particleId);
    }

    /**
     * PacketCommon的消息处理器类<br>
     * 负责在接收到数据包时在客户端生成粒子效果
     */
    public static class Handle implements IMessageHandler<PacketCommon, IMessage> {

        /**
         * 处理接收到的PacketCommon消息
         * @param message 接收到的数据包
         * @param ctx 消息上下文
         * @return 返回响应消息，此处为null表示无响应
         */
        @Override
        public IMessage onMessage(PacketCommon message, MessageContext ctx) {
            // 只在客户端执行粒子生成
            if (ctx.side.isClient()) {
                World world = Minecraft.getMinecraft().world;
                // 根据粒子ID获取对应的粒子类型
                EnumParticleTypes particle = EnumParticleTypes.values()[message.particleId];
                // 在指定位置生成粒子
                world.spawnParticle(particle, message.x, message.y, message.z, 0.0, 0.0, 0.0);
            }
            return null;
        }
    }
}