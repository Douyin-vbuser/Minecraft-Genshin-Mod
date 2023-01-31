package com.vbuser.genshin.gui;

import com.vbuser.genshin.Main;
import net.minecraft.client.gui.inventory.GuiCrafting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

public class ModGuiLoader implements IGuiHandler {
    public static final int HE_CHENG_TAI_ID = 1;

    public ModGuiLoader(){
        NetworkRegistry.INSTANCE.registerGuiHandler(Main.instance,this);
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z){
        switch (ID) {
            case HE_CHENG_TAI_ID:return new ContainerHeChengTai(player.inventory,world,new BlockPos(x,y,z));
            default:return null;
        }
    }

    @Override
    public Object getClientGuiElement(int ID,EntityPlayer player,World world,int x,int y,int z){
        switch (ID){
            case HE_CHENG_TAI_ID:return new GuiCrafting(player.inventory,world,new BlockPos(x,y,z));
            default:return null;
        }
    }
}
