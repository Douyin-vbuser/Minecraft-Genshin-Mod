package com.vbuser.inventory.gui;

import com.vbuser.inventory.CustomInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class ModGuiLoader implements IGuiHandler {

    public ModGuiLoader(){
        NetworkRegistry.INSTANCE.registerGuiHandler(CustomInventory.instance,this);
    }

    public static final int customInventory = 1;
    public static final int character = 2;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case customInventory:return new GuiInventory();
            case character:
            default:return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case customInventory:return new GuiInventory();
            case character:
            default:return null;
        }
    }
}
