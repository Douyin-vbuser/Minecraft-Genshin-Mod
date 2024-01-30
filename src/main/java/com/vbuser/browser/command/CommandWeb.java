package com.vbuser.browser.command;

import com.vbuser.browser.Browser;
import com.vbuser.browser.network.PacketWebPage;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

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
        Browser.network.sendTo(new PacketWebPage(url), player);
    }
}
