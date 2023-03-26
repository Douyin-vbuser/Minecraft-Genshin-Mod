package com.vbuser.genshin.gui;

import com.vbuser.genshin.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

public class GuiTexturedButton extends GuiButton {
    private final ResourceLocation pathNormal;
    private final ResourceLocation pathHover;
    private final ResourceLocation pathLocked;

    private final int textureX;
    private final int textureY;

    public GuiTexturedButton(int buttonId, int x, int y, int widthIn, int heightIn, String path_normal, String path_hover, String path_locked, int textureX, int textureY) {
        super(buttonId, x, y, widthIn, heightIn, "");
        this.textureX = textureX;
        this.textureY = textureY;
        this.pathNormal = new ResourceLocation(Reference.Mod_ID, path_normal);
        this.pathHover = new ResourceLocation(Reference.Mod_ID, path_hover);
        this.pathLocked = new ResourceLocation(Reference.Mod_ID, path_locked);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            boolean mouseHover = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            if (mouseHover && this.enabled) {
                mc.getTextureManager().bindTexture(this.pathHover);

            } else if (this.enabled) {
                mc.getTextureManager().bindTexture(this.pathNormal);
            } else {
                mc.getTextureManager().bindTexture(this.pathLocked);
            }
            FontRenderer fontrenderer = mc.fontRenderer;


            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
        } else {
            mc.getTextureManager().bindTexture(this.pathHover);
            FontRenderer fontrenderer = mc.fontRenderer;
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawModalRectWithCustomSizedTexture(this.x, this.y, 0, 0, this.width, this.height, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
        }
    }
}