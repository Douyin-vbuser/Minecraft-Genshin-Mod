package com.vbuser.browser.gui;

import com.vbuser.browser.Browser;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.montoyo.mcef.api.API;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.MCEFApi;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
public class GuiCG extends GuiScreen {
    IBrowser browser = null;
    private GuiTextField url = null;
    private String urlToLoad;

    public GuiCG() {
    }

    public GuiCG(String url) {
        this.urlToLoad = url;
    }

    public void initGui() {
        if (this.browser == null) {
            API api = MCEFApi.getAPI();
            if (api == null) {
                return;
            }
            this.browser = api.createBrowser(urlToLoad, true);
            this.urlToLoad = null;
        }

        if (this.browser != null) {
            this.browser.resize(this.mc.displayWidth, this.mc.displayHeight);
        }

        Keyboard.enableRepeatEvents(true);
        this.buttonList.clear();
        if (this.url == null) {
            this.url = new GuiTextField(5, this.fontRenderer, 0, 0, 0, 0);
            this.url.setMaxStringLength(65535);
        } else {
            String old = this.url.getText();
            this.url = new GuiTextField(5, this.fontRenderer, 0, 0, 0, 0);
            this.url.setMaxStringLength(65535);
            this.url.setText(old);
        }

    }

    public void updateScreen() {
        if (this.urlToLoad != null && this.browser != null) {
            this.browser.loadURL(this.urlToLoad);
            this.urlToLoad = null;
        }

    }

    public void onGuiClosed() {
        this.browser.close();
        Browser.path = null;
        Browser.video = "";
        Keyboard.enableRepeatEvents(false);
    }

    public void drawScreen(int i1, int i2, float f) {
        super.drawScreen(i1, i2, f);
        if (this.browser != null) {
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.browser.draw(0.0D, this.height, this.width, 0.0D);
            GlStateManager.enableDepth();
        }
    }
}
