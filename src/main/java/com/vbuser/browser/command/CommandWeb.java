package com.vbuser.browser.command;

import com.vbuser.browser.Browser;
import com.vbuser.browser.network.PacketWebPage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class CommandWeb extends CommandBase {
    @Override
    public String getName() {
        return "web";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "web <player> <url>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getPlayer(server,sender,args[0]);
        String url = args[1];
        String ip = "127.0.0.1";
        if(server.getServer() !=null && url.contains("server_ip")) {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        if(args[1].contains("server_ip"))player.sendMessage(new net.minecraft.util.text.TextComponentString("redirecting \"server_ip\" to " + ip));
        url = url.replace("server_ip",ip);
        Browser.network.sendTo(new PacketWebPage(url), player);
    }
}
