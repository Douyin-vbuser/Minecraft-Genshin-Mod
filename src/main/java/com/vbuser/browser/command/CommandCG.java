package com.vbuser.browser.command;

import com.vbuser.browser.Browser;
import com.vbuser.browser.network.PacketVideo;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

public class CommandCG extends CommandBase {
    @Override
    public String getName() {
        return "cg";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "cg <player> <video>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getPlayer(server, sender, args[0]);
        String video = args[1];
        Browser.video = video;
        Browser.network.sendTo(new PacketVideo(video), player);
    }
}
