package com.vbuser.particulate.commad;

import com.vbuser.particulate.Particulate;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CmdB extends CommandBase {
    @Override
    public String getName() {
        return "cmdb";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "cmdb <player> <x> <y> <z> <name> <meta>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayerMP player = getPlayer(server,sender,args[0]);
        int x = Integer.parseInt(args[1]);
        int y = Integer.parseInt(args[2]);
        int z = Integer.parseInt(args[3]);
        BlockPos pos = new BlockPos(x,y,z);
        Block block = Block.getBlockFromName(args[4]);
        int meta = Integer.parseInt(args[5]);
        Particulate.renderBlock(player,pos,block,meta);
    }
}
