package com.vbuser.movement.command;

import com.vbuser.movement.event.FOVHandler;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

@SuppressWarnings("all")
public class FOVCommand extends CommandBase {
    @Override
    public String getName() {
        return "fov";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "fov <player> <operate> <num>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length == 3) {
            EntityPlayerMP player = getPlayer(server, sender, args[0]);
            String operate = args[1];
            int num = Integer.parseInt(args[2]);
            if (operate.equalsIgnoreCase("set")) {
                FOVHandler.sp.put(player, num);
            } else if (operate.equalsIgnoreCase("add")) {
                FOVHandler.sp.put(player, FOVHandler.getFOV(player) + num);
            }
        } else if (args.length == 2) {
            EntityPlayerMP player = getPlayer(server, sender, args[0]);
            String operate = args[1];
            if (operate.equalsIgnoreCase("reset"))
                FOVHandler.sp.remove(player);
        }
    }
}
