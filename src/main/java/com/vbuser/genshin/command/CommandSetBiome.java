package com.vbuser.genshin.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketChunkData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

import java.util.Arrays;

@SuppressWarnings("all")
public class CommandSetBiome extends CommandBase {
    @Override
    public String getName() {
        return "biome";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "biome <id>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("biome <id>");
        }

        EntityPlayer player = getCommandSenderAsPlayer(sender);
        World world = player.world;
        Chunk chunk = world.getChunkFromBlockCoords(player.getPosition());

        byte[] biomeArray = new byte[256];
        byte biomeId = (byte) Integer.parseInt(args[0]);
        Arrays.fill(biomeArray, biomeId);
        chunk.setBiomeArray(biomeArray);
        chunk.setModified(true);

        world.getChunkProvider().provideChunk(chunk.x, chunk.z);
        world.markChunkDirty(chunk.getPos().getBlock(0, 0, 0), null);

        if (!world.isRemote) {
            SPacketChunkData packet = new SPacketChunkData(chunk, 65535);
            for (EntityPlayer playerInWorld : world.playerEntities) {
                if (playerInWorld instanceof EntityPlayerMP) {
                    EntityPlayerMP playerMP = (EntityPlayerMP) playerInWorld;
                    double distance = playerMP.getDistanceSq(chunk.x * 16 + 8, playerMP.posY, chunk.z * 16 + 8);
                    if (distance < 1024) {
                        playerMP.connection.sendPacket(packet);
                    }
                }
            }
        }

        notifyCommandListener(sender, this, "commands.biome.success", chunk.x, chunk.z, biomeId);
    }
}