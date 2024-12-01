package com.vbuser.genshin.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Arrays;

public class CommandSetBiome extends CommandBase {
    @Override
    public String getName() {
        return "biome";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        EntityPlayer player = getCommandSenderAsPlayer(sender);
        World world = player.world;
        Chunk chunk = world.getChunkFromBlockCoords(player.getPosition());

        byte[] biomeArray = new byte[256];
        byte biomeId = (byte) Integer.parseInt(args[0]);
        Arrays.fill(biomeArray, biomeId);
        chunk.setBiomeArray(biomeArray);
        chunk.setModified(true);

        if (!world.isRemote) {
            world.getChunkProvider().provideChunk(chunk.x, chunk.z);
        }
    }
}
