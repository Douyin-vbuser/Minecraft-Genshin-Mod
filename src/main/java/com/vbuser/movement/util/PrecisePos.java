package com.vbuser.movement.util;

import net.minecraft.entity.player.EntityPlayer;

public class PrecisePos {
    private final double x,y,z;

    public PrecisePos(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public PrecisePos(EntityPlayer player){
        this.x = player.posX;
        this.y = player.posY;
        this.z = player.posZ;
    }

    public double getX(){
        return x;
    }

    public double getY(){
        return y;
    }

    public double getZ(){
        return z;
    }
}
