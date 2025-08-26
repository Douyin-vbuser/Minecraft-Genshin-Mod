package com.vbuser.particulate.network.particle;

import com.vbuser.particulate.render.particulate.ParticulateSimple;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * PacketSimple 类用于在客户端和服务器之间传输自定义粒子效果信息<br>
 * 实现了IMessage接口，用于网络序列化和反序列化<br>
 * 支持简单的粒子效果和基于数学表达式的复杂粒子效果
 */
public class PacketSimple implements IMessage {

    public double x, y, z;  // 粒子生成位置的坐标
    public boolean hasExpr; // 是否包含表达式定义
    public String exX, exY, exZ; // 位置表达式 (x, y, z坐标)
    public String exR, exG, exB, exA; // 颜色表达式 (红, 绿, 蓝, 透明度)
    public String exS;      // 尺寸表达式
    public int life;        // 粒子生命周期

    /**
     * 无参构造函数，Forge网络框架需要
     */
    public PacketSimple() {}

    /**
     * 带表达式参数的构造函数，用于创建复杂的自定义粒子
     * @param x 粒子初始x坐标
     * @param y 粒子初始y坐标
     * @param z 粒子初始z坐标
     * @param exX x坐标随时间变化的表达式
     * @param exY y坐标随时间变化的表达式
     * @param exZ z坐标随时间变化的表达式
     * @param exR 红色分量随时间变化的表达式
     * @param exG 绿色分量随时间变化的表达式
     * @param exB 蓝色分量随时间变化的表达式
     * @param exA 透明度随时间变化的表达式
     * @param exS 尺寸随时间变化的表达式
     * @param life 粒子生命周期（持续时间）
     */
    public PacketSimple(double x, double y, double z,
                        String exX, String exY, String exZ,
                        String exR, String exG, String exB, String exA,
                        String exS, int life) {
        this.x=x; this.y=y; this.z=z;
        this.hasExpr=true;
        this.exX=exX; this.exY=exY; this.exZ=exZ;
        this.exR=exR; this.exG=exG; this.exB=exB; this.exA=exA;
        this.exS=exS; this.life=life;
    }

    /**
     * 简单粒子效果的构造函数
     * @param x 粒子生成的x坐标
     * @param y 粒子生成的y坐标
     * @param z 粒子生成的z坐标
     */
    public PacketSimple(double x, double y, double z) {
        this.x=x; this.y=y; this.z=z;
        this.hasExpr=false;
    }

    /**
     * 从字节缓冲区读取数据，反序列化数据包<br>
     * 读取顺序必须与toBytes方法的写入顺序一致
     * @param b 字节缓冲区
     */
    @Override
    public void fromBytes(ByteBuf b) {
        x=b.readDouble(); y=b.readDouble(); z=b.readDouble();
        hasExpr=b.readBoolean();
        if(hasExpr){
            exX=ByteBufUtils.readUTF8String(b);
            exY=ByteBufUtils.readUTF8String(b);
            exZ=ByteBufUtils.readUTF8String(b);
            exR=ByteBufUtils.readUTF8String(b);
            exG=ByteBufUtils.readUTF8String(b);
            exB=ByteBufUtils.readUTF8String(b);
            exA=ByteBufUtils.readUTF8String(b);
            exS=ByteBufUtils.readUTF8String(b);
            life=b.readInt();
        }
    }

    /**
     * 将数据写入字节缓冲区，序列化数据包<br>
     * 写入顺序必须与fromBytes方法的读取顺序一致
     * @param b 字节缓冲区
     */
    @Override
    public void toBytes(ByteBuf b) {
        b.writeDouble(x); b.writeDouble(y); b.writeDouble(z);
        b.writeBoolean(hasExpr);
        if(hasExpr){
            ByteBufUtils.writeUTF8String(b,exX);
            ByteBufUtils.writeUTF8String(b,exY);
            ByteBufUtils.writeUTF8String(b,exZ);
            ByteBufUtils.writeUTF8String(b,exR);
            ByteBufUtils.writeUTF8String(b,exG);
            ByteBufUtils.writeUTF8String(b,exB);
            ByteBufUtils.writeUTF8String(b,exA);
            ByteBufUtils.writeUTF8String(b,exS);
            b.writeInt(life);
        }
    }

    /**
     * PacketSimple的消息处理器类<br>
     * 负责在接收到数据包时在客户端生成相应的粒子效果
     */
    public static class Handler implements IMessageHandler<PacketSimple,IMessage>{

        /**
         * 处理接收到的PacketSimple消息
         * @param m 接收到的数据包
         * @param c 消息上下文
         * @return 返回响应消息，此处为null表示无响应
         */
        @Override
        public IMessage onMessage(PacketSimple m, MessageContext c) {
            // 只在客户端执行粒子效果添加
            if(c.side.isClient()){
                World w=Minecraft.getMinecraft().world;
                // 安排任务在主线程执行粒子添加
                Minecraft.getMinecraft().addScheduledTask(()->{
                    if(m.hasExpr){
                        // 添加带有表达式的复杂粒子效果
                        Minecraft.getMinecraft().effectRenderer.addEffect(
                                new ParticulateSimple(w,m.x,m.y,m.z,
                                        m.exX,m.exY,m.exZ,m.exR,m.exG,m.exB,m.exA,m.exS,m.life));
                    }else{
                        // 添加简单粒子效果
                        Minecraft.getMinecraft().effectRenderer.addEffect(
                                new ParticulateSimple(w,m.x,m.y,m.z));
                    }
                });
            }
            return null;
        }
    }
}