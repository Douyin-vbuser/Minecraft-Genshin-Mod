package com.vbuser.genshin.command;

import com.vbuser.genshin.event.Task;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandTask extends CommandBase {
    @Override
    public String getName() {
        return "task";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "task <operate> <task>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args[0].equals("start")) {
            Task.setTask(args[1], getCommandSenderAsPlayer(sender));
        } else if (args[0].equals("terminate")) {
            Task.terminateTask();
        }
    }
}
