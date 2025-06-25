package com.vbuser.genshin.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Teleporter;
import net.minecraft.world.WorldServer;

@SuppressWarnings("all")
public class CommandTpDim extends CommandBase {

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getName() {
        return "tpdimension";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "tpdimension <id> <x> <y> <z>";
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
        int d, x, y, z;
        try {
            d = Integer.parseInt(args[0]);
            x = Integer.parseInt(args[1]);
            y = Integer.parseInt(args[2]);
            z = Integer.parseInt(args[3]);
        } catch (NumberFormatException e) {
            return;
        }

        if (sender instanceof EntityPlayer) {
            Teleport.teleportToDim((EntityPlayer) sender, d, x, y, z);
        }
    }

    public static class Teleport extends Teleporter {
        private final WorldServer worldServer;
        private double x, y, z;

        public Teleport(WorldServer worldServer, double x, double y, double z) {
            super(worldServer);
            this.worldServer = worldServer;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public void placeInPortal(Entity entityIn, float rotationYaw) {
            worldServer.getBlockState(new BlockPos(x, y, z));
            entityIn.setPosition(x, y, z);
            entityIn.motionX = 0f;
            entityIn.motionY = 0f;
            entityIn.motionZ = 0f;

        }

        public static void teleportToDim(EntityPlayer player, int dimension, double x, double y, double z) {
            EntityPlayerMP entityPlayerMP = (EntityPlayerMP) player;
            MinecraftServer server = player.getEntityWorld().getMinecraftServer();
            if (server == null) {
                throw new IllegalArgumentException("Player status incorrect");
            }

            WorldServer worldServerNew = server.getWorld(dimension);
            if (worldServerNew == null) {
                throw new IllegalArgumentException(String.format("[Err]Teleporting dimension: %d does not exist", dimension));
            }

            worldServerNew.getMinecraftServer().getPlayerList().transferPlayerToDimension(entityPlayerMP, dimension, new Teleport(worldServerNew, x, y, z));
            player.setPositionAndUpdate(x, y, z);
        }
    }
}

