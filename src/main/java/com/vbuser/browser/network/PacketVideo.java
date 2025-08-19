package com.vbuser.browser.network;

import com.vbuser.browser.Browser;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketVideo implements IMessage {

    String video;

    public PacketVideo() {
    }

    public PacketVideo(String video) {
        this.video = video;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        this.video = packetBuffer.readString(114);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeString(video);
    }

    public static class PacketVideoHandler implements IMessageHandler<PacketVideo, IMessage> {
        @Override
        public IMessage onMessage(PacketVideo message, MessageContext ctx) {
            String mcDir = Minecraft.getMinecraft().mcDataDir.getAbsolutePath();
            String temp = (mcDir.endsWith("\\") ? mcDir : mcDir + "\\").replace("\\", "/");
            temp = temp.substring(0, temp.length() - 3);
            Browser.path = "file:///" + temp + "/cg/" + Browser.video + ".webm";
            System.out.println(Browser.path);
            Browser.isCG = true;
            BlockPos pos = Minecraft.getMinecraft().player.getPosition();
            Minecraft.getMinecraft().player.openGui(Browser.instance, 13, Minecraft.getMinecraft().world, pos.getX(), pos.getY(), pos.getZ());
            return null;
        }
    }
}
