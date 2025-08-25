package com.vbuser.particulate.network.particle;

import com.vbuser.particulate.render.particulate.ParticulateSimple;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketSimple implements IMessage {

    public double x, y, z;
    public boolean hasExpr;
    public String exX, exY, exZ, exR, exG, exB, exA, exS;
    public int life;

    public PacketSimple() {}

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

    public PacketSimple(double x, double y, double z) {
        this.x=x; this.y=y; this.z=z;
        this.hasExpr=false;
    }

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

    public static class Handler implements IMessageHandler<PacketSimple,IMessage>{
        @Override
        public IMessage onMessage(PacketSimple m, MessageContext c) {
            if(c.side.isClient()){
                World w=Minecraft.getMinecraft().world;
                Minecraft.getMinecraft().addScheduledTask(()->{
                    if(m.hasExpr){
                        Minecraft.getMinecraft().effectRenderer.addEffect(
                                new ParticulateSimple(w,m.x,m.y,m.z,
                                        m.exX,m.exY,m.exZ,m.exR,m.exG,m.exB,m.exA,m.exS,m.life));
                    }else{
                        Minecraft.getMinecraft().effectRenderer.addEffect(
                                new ParticulateSimple(w,m.x,m.y,m.z));
                    }
                });
            }
            return null;
        }
    }
}