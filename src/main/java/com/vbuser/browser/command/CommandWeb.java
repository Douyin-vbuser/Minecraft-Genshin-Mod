package com.vbuser.browser.command;

import com.vbuser.browser.Browser;
import com.vbuser.browser.network.PacketWebPage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;

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
        EntityPlayerMP player = getPlayer(server, sender, args[0]);
        String url = args[1];
        String ip = "127.0.0.1";

        File serverIpFile = new File("server_ip.txt");
        if (serverIpFile.exists() && serverIpFile.isFile()) {
            try {
                String content = new String(Files.readAllBytes(serverIpFile.toPath())).trim();
                if (content.matches("^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$")) {
                    ip = content;
                } else {
                    server.logWarning("Invalid IP address format in server_ip.txt: " + content);
                }
            } catch (IOException e) {
                throw new CommandException("Failed to read server_ip.txt: " + e.getMessage());
            }
        } else {
            try {
                InetAddress inetAddress = InetAddress.getLocalHost();
                ip = inetAddress.getHostAddress();
            } catch (UnknownHostException e) {
                throw new CommandException("Failed to get local IP address: " + e.getMessage());
            }
        }

        if (url.contains("server_ip")) {
            player.sendMessage(new TextComponentString("Redirecting \"server_ip\" to " + ip));
            url = url.replace("server_ip", ip);
        }

        Browser.network.sendTo(new PacketWebPage(url), player);
    }
}
