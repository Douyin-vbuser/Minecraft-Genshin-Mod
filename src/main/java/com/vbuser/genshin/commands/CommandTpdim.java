package com.vbuser.genshin.commands;

import com.google.common.collect.Lists;
import com.vbuser.genshin.util.Reference;
import com.vbuser.genshin.util.Teleport;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;

import java.util.List;

/**
 * 根据（抄袭）理想境架构的tpdim指令,稍作修改
 **/

@SuppressWarnings("all")
public class CommandTpdim extends CommandBase {

    private final List<String> aliases = Lists.newArrayList("tp", "tpdim", "chuansong");

    @Override
    public String getName() {
        return "tpdimension";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "tpdimension <id> <x> <y> <z>";
    }

    @Override
    public List<String> getAliases() {
        return aliases;
    }


    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
        return true;
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        if (args.length < 1) {
            return;
        }

        String s = args[0];
        int dimensionID;
        String s1 = args[1];
        int x;
        String s2 = args[2];
        int y;
        String s3 = args[3];
        int z;
        try{
            dimensionID = Integer.parseInt(s);
            x=Integer.parseInt(s1);
            y=Integer.parseInt(s2);
            z=Integer.parseInt(s3);
        }catch (NumberFormatException e)
        {
            return;
        }

        if (sender instanceof EntityPlayer)
        {
            try{
                Teleport.teleportToDim((EntityPlayer) sender, dimensionID, x, y, z);
            }
            catch (IllegalArgumentException e)
            {
                throw e;
            }
        }
    }
}