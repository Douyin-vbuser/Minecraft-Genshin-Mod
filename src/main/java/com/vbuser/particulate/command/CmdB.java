package com.vbuser.particulate.command;

import com.vbuser.particulate.Particulate;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("all")
public class CmdB extends CommandBase {
    @Override
    public String getName() {
        return "cmdb";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "cmdb <player> <command> <x> <y> <z> <name> <meta>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getPlayer(server, sender, args[0]);
        if (args[1].equals("fill")) {
            int x_from = Integer.parseInt(args[2]);
            int y_from = Integer.parseInt(args[3]);
            int z_from = Integer.parseInt(args[4]);
            int x_to = Integer.parseInt(args[5]);
            int y_to = Integer.parseInt(args[6]);
            int z_to = Integer.parseInt(args[7]);
            Block block = Block.getBlockFromName(args[8]);
            int meta = Integer.parseInt(args[9]);
            for (int i = Math.min(x_from, x_to); i <= Math.max(x_from, x_to); i++) {
                for (int j = Math.min(y_from, y_to); j <= Math.max(y_from, y_to); j++) {
                    for (int k = Math.min(z_from, z_to); k <= Math.max(z_from, z_to); k++) {
                        BlockPos pos = new BlockPos(i, j, k);
                        Particulate.renderBlock(player, pos, block, meta);
                    }
                }
            }
        } else {
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int z = Integer.parseInt(args[4]);
            BlockPos pos = new BlockPos(x, y, z);
            Block block = Block.getBlockFromName(args[5]);
            int meta = Integer.parseInt(args[6]);
            Particulate.renderBlock(player, pos, block, meta);
        }
    }
}
