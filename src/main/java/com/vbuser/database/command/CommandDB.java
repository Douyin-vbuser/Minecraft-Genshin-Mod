package com.vbuser.database.command;

import com.vbuser.database.operate.Console;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

import java.io.File;
import java.io.IOException;

public class CommandDB extends CommandBase {
    @Override
    public String getName() {
        return "db";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "db";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        String command = String.join(" ",args);
        File temp = getCommandSenderAsPlayer(sender).getServerWorld().getSaveHandler().getWorldDirectory();
        command = command.replace("saves",temp.getAbsolutePath());
        try {
            Console.setDataBase(new File(temp,"genshin_data"));
            Console.executeCommand(command);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
