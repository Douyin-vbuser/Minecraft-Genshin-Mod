package com.vbuser.database.command;

import com.vbuser.database.DataBase;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;

public class CommandOperate extends CommandBase {
    @Override
    public String getName() {
        return "ope";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "ope <performance> <player> <item> <count>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args[0].equals("add")){
            EntityPlayer player = getPlayer(server, sender, args[1]);
            Item item = Item.getByNameOrId("genshin:"+args[2]);
            assert item != null;
            DataBase.addItem(item, Integer.parseInt(args[3]), player);
        }
    }
}
