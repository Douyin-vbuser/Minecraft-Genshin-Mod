package com.vbuser.genshin.inventory.command;

import com.vbuser.genshin.inventory.CustomInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class CommandInventory extends CommandBase {

    @Override
    public String getName() {
        return "inventory";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "inventory <performance> <player> <item> <count>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if(args.length == 4){
            if(args[0].equals("add")){
                EntityPlayer player = getPlayer(server, sender, args[1]);
                Item item = Item.getByNameOrId(args[2]);
                if (item != null) {
                    boolean result = CustomInventory.addItem(player.getUniqueID(),item, Integer.parseInt(args[3]));
                    if(!result){
                        player.sendMessage(new TextComponentString(I18n.format("inventory.error.add")));
                    }
                }else{
                    player.sendMessage(new TextComponentString(I18n.format("inventory.error.item")));
                }
            }
            else if(args[0].equals("min")){
                EntityPlayer player = getPlayer(server, sender, args[1]);
                Item item = Item.getByNameOrId(args[2]);
                if (item != null) {
                    boolean result = CustomInventory.deleteItem(player.getUniqueID(),args[2], Integer.parseInt(args[3]));
                    if(!result){
                        player.sendMessage(new TextComponentString(I18n.format("inventory.error.min")));
                    }
                }else{
                    player.sendMessage(new TextComponentString(I18n.format("inventory.error.item")));
                }
            }
            else{
                getCommandSenderAsPlayer(sender).sendMessage(new TextComponentString(I18n.format("inventory.error.performance")));
            }
        }
    }
}
