package com.vbuser.genshin.command;

import com.vbuser.movement.event.PlayerListener;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandInit extends CommandBase {
    @Override
    public String getName() {
        return "init";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "init";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        PlayerListener.initTable(getCommandSenderAsPlayer(sender));
    }
}
