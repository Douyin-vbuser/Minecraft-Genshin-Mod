package com.vbuser.browser.command;

import com.vbuser.browser.server.Server;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandStartServer extends CommandBase {
    @Override
    public String getName() {
        return "start_server";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "start_server";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args){
        try {
            Server.start();
            sender.sendMessage(new TextComponentString("Server started on port 80."));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
