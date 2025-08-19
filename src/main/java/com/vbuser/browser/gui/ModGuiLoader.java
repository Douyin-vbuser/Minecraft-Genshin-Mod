package com.vbuser.browser.gui;

import com.vbuser.browser.Browser;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import javax.annotation.Nullable;

public class ModGuiLoader implements IGuiHandler {

    public ModGuiLoader() {
        NetworkRegistry.INSTANCE.registerGuiHandler(Browser.instance, this);
    }

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 11:
                return new GuiCG();
            case 12:
                return new GuiWeb();
            case 13:
                return new GuiIndex();
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
        switch (ID) {
            case 11:
                return new GuiCG();
            case 12:
                return new GuiWeb();
            case 13:
                return new GuiIndex();
            default:
                return null;
        }
    }
}
