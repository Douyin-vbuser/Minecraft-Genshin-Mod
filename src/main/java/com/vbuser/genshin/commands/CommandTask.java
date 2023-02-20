package com.vbuser.genshin.commands;

import net.minecraft.client.resources.I18n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketCustomSound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.SoundCategory;

/**example:
 * input: /task @a say feng_yuan_wan_ye main.0.1
 * output:[14:59:36] [Client thread/INFO] [minecraft/GuiNewChat]: [CHAT] <枫原万叶>哇
 * 注：这个音频是派蒙的
*/
public class CommandTask extends CommandBase {
    @Override
    public String getName() {
        return "task";
    }

    @Override
    public String getUsage(ICommandSender sender){
        return "task <player> <function> <character> <task_id>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP entityplayermp = getPlayer(server, sender, args[0]);
        String s2 = args[1];
        String function;
        String s = args[2];
        String character;
        String s1 = args[3];
        String task_id;
        try {
            character = s;
            task_id = s1;
            function=s2;
        }
        catch (NumberFormatException e)
        {
            return;
        }
        if (sender instanceof EntityPlayer)
        {
            try{
                if(function.equals("say")) {
                    entityplayermp.connection.sendPacket(new SPacketCustomSound("genshin:"+task_id, SoundCategory.PLAYERS, ((EntityPlayer) sender).posX, ((EntityPlayer) sender).posY, ((EntityPlayer) sender).posZ, 3, 1));
                    notifyCommandListener(sender, this, ("<" + I18n.format(character) + ">" + I18n.format(task_id)));
                }
                if(function.equals("start")){
                    //TODO
                }
                if(function.equals("end")){
                    //TODO
                }
                if(function.equals("cg")){
                    //TODO
                }
                else{
                    notifyCommandListener(sender,this,"wrong_command_usage");
                }
            }
            catch (IllegalArgumentException e)
            {
                notifyCommandListener(sender,this,"wrong_command_usage");
            }
        }
    }
}
