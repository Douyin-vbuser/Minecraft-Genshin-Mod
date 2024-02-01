package com.vbuser.browser.network;

import com.vbuser.browser.Browser;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class PacketServerIP implements IMessage {
    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    public static class PacketServerIPHandler implements IMessageHandler<PacketServerIP, IMessage> {
       @Override
        public IMessage onMessage(PacketServerIP message, MessageContext ctx){
           InetAddress localhost;
           try {
               localhost = InetAddress.getLocalHost();
           } catch (UnknownHostException e) {
               throw new RuntimeException(e);
           }
           InetAddress[] addresses;
           try {
               addresses = InetAddress.getAllByName(localhost.getCanonicalHostName());
           } catch (UnknownHostException e) {
               throw new RuntimeException(e);
           }
           for (InetAddress address : addresses) {
               if (address.isSiteLocalAddress() && !address.isLoopbackAddress()) {
                   Browser.serverIP = address.getHostAddress();
               }
           }
           Browser.lock = true;
           return null;
       }
    }
}
