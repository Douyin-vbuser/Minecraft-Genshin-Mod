package com.vbuser.browser.network;

import com.vbuser.browser.Browser;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketWebPage implements IMessage {

    String url;

    public PacketWebPage() {
    }

    public PacketWebPage(String url) {
        this.url = url;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        this.url = packetBuffer.readString(1919810);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeString(this.url);
    }

    public static class PacketWebPageHandler implements IMessageHandler<PacketWebPage, IMessage> {

        @Override
        public IMessage onMessage(PacketWebPage message, MessageContext ctx) {
            Browser.path = message.url;
            Browser.isCG = false;
            BlockPos pos = Minecraft.getMinecraft().player.getPosition();
            Minecraft.getMinecraft().player.openGui(Browser.instance, 13, Minecraft.getMinecraft().world, pos.getX(), pos.getY(), pos.getZ());
            return null;
        }
    }
}
