package com.vbuser.particulate.command;

import com.vbuser.particulate.Particulate;
import com.vbuser.particulate.network.particle.PacketCommon;
import com.vbuser.particulate.network.particle.PacketSimple;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

@SuppressWarnings("all")
public class CmdP extends CommandBase {
    @Override
    public String getName() {
        return "cmdp";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "cmdp <id> <x> <y> <z>";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 4) {
            throw new CommandException("Invalid number of arguments. Usage: " + getUsage(sender));
        }

        try {
            int particleId = Integer.parseInt(args[0]);
            double x = parseDouble(args[1]);
            double y = parseDouble(args[2]);
            double z = parseDouble(args[3]);

            if (particleId == 50) {
                Particulate.networkWrapper.sendToAll(new PacketSimple(x, y, z));
                return;
            }

            EnumParticleTypes particle = EnumParticleTypes.values()[particleId];
            World world = sender.getEntityWorld();
            world.spawnParticle(particle, x, y, z, 0.0, 0.0, 0.0);
            Particulate.networkWrapper.sendToAll(new PacketCommon(x, y, z, particleId));
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid number format. Usage: " + getUsage(sender));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommandException("Invalid particle ID. Usage: " + getUsage(sender));
        }
    }
}
