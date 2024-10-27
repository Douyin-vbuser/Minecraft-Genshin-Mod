package com.vbuser.genshin.command;

import com.vbuser.genshin.event.CharacterChoice;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import java.util.UUID;

public class CommandChar extends CommandBase {
    @Override
    public String getName() {
        return "char";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "char player slot character";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length==0){
            sender.sendMessage(new TextComponentString(CharacterChoice.get().toString()));
        }else{
            UUID player = getPlayer(server,sender,args[0]).getUniqueID();
            int slot = Integer.parseInt(args[1]);
            int character = Integer.parseInt(args[2]);
            CharacterChoice.set(player,slot,character);
        }
    }
}
