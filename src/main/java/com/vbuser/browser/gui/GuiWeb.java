package com.vbuser.browser.gui;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.montoyo.mcef.api.API;
import net.montoyo.mcef.api.IBrowser;
import net.montoyo.mcef.api.MCEFApi;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

@SideOnly(Side.CLIENT)
public class GuiWeb extends GuiScreen {
    IBrowser browser = null;
    private GuiTextField url = null;
    private String urlToLoad;

    public GuiWeb() {
    }

    public GuiWeb(String url) {
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

    public int scaleY(int y) {
        double sy = (double) y / (double) this.height * (double) this.mc.displayHeight;
        return (int) sy;
    }


    public void updateScreen() {
        if (this.urlToLoad != null && this.browser != null) {
            this.browser.loadURL(this.urlToLoad);
            this.urlToLoad = null;
        }

    }

    public void onGuiClosed() {
        this.browser.close();
        Keyboard.enableRepeatEvents(false);
    }

    public void drawScreen(int i1, int i2, float f) {
        drawDefaultBackground();
        this.url.drawTextBox();
        super.drawScreen(i1, i2, f);
        if (this.browser != null) {
            GlStateManager.disableDepth();
            GlStateManager.enableTexture2D();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.browser.draw(0.0D, this.height, this.width, 0.0D);
            GlStateManager.enableDepth();
        }

    }


    public void handleInput() {
        boolean pressed;
        int num;
        while (Keyboard.next()) {
            if (Keyboard.getEventKey() == 1) {
                this.mc.displayGuiScreen(null);
                return;
            }

            pressed = this.url.isFocused();
            char key = Keyboard.getEventCharacter();
            num = Keyboard.getEventKey();
            if (this.browser != null && !pressed) {
                this.browser.injectKeyReleasedByKeyCode(num, key, 0);

                int code = 0;
                if (key != 0) {
                    this.browser.injectKeyTyped(key, code);
                }
            }
        }

        while (Mouse.next()) {
            int btn = Mouse.getEventButton();
            pressed = Mouse.getEventButtonState();
            int sx = Mouse.getEventX();
            num = Mouse.getEventY();
            int wheel = Mouse.getEventDWheel();
            int y;
            if (this.browser != null) {
                y = this.mc.displayHeight - num - this.scaleY(0);
                if (wheel != 0) {
                    this.browser.injectMouseWheel(sx, y, 0, 1, wheel);
                } else if (btn == -1) {
                    this.browser.injectMouseMove(sx, y, 0, y < 0);
                } else {
                    byte btn_;
                    if (btn == 0) {
                        btn_ = 1;
                    } else if (btn == 1) {
                        btn_ = 3;
                    } else {
                        btn_ = 2;
                    }

                    this.browser.injectMouseButton(sx, y, 0, btn_, pressed, 1);
                }
            }
        }

    }
}
