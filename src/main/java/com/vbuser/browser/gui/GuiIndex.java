package com.vbuser.browser.gui;

import com.vbuser.browser.Browser;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiIndex extends GuiScreen {

    private final boolean isCG;

    public GuiIndex() {
        isCG = Browser.isCG;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawDefaultBackground();
        if (isCG) {
            mc.displayGuiScreen(new GuiCG(Browser.path));
        } else {
            mc.displayGuiScreen(new GuiWeb(Browser.path));
        }
    }

    //I really don't know why this is fucking needed, but it is.
}
