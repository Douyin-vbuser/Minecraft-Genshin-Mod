package com.vbuser.movement;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class Storage {
    public static int start_x,start_y,start_z,end_x,end_y,end_z,last_time,start_yaw,end_yaw;
    public static boolean pack_received = false;
    public static boolean is_performing = false;
}
