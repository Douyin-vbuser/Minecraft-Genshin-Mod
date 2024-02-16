package com.vbuser.movement.command;

import com.vbuser.movement.Movement;
import com.vbuser.movement.network.PacketCameraTrack;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class CommandCameraTrack extends CommandBase {
    @Override
    public String getName() {
        return "camera_track";
    }

    @Override
    public String getUsage(ICommandSender sender) {
        return "camera_track <start_x> <start_y> <start_z> <end_x> <end_y> <end_z> <time_last> <start_yaw> <end_yaw>";
        // test command: /camera_track 11 4 5 14 19 19 81 0 0
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) {
        Movement.network.sendToAll(new PacketCameraTrack(Integer.parseInt(args[0]),Integer.parseInt(args[1]), Integer.parseInt(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), Integer.parseInt(args[6]), Integer.parseInt(args[7]),Integer.parseInt(args[8])));
    }
}
