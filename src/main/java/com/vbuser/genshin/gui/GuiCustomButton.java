package com.vbuser.genshin.gui;

import com.vbuser.genshin.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
public class GuiCustomButton extends GuiButton {
    private final ResourceLocation buttonTex = new ResourceLocation(Reference.Mod_ID, "textures/gui/gui_button.png");
    private final int textureX;
    private final int textureY;

    public GuiCustomButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, int textureX, int textureY) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.textureX = textureX;
        this.textureY = textureY;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
        if (this.visible) {
            FontRenderer fontrenderer = mc.fontRenderer;
            mc.getTextureManager().bindTexture(buttonTex);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
            int i = this.getHoverState(this.hovered);
            this.drawTexturedModalRect(this.x, this.y, this.textureX, this.textureY + i * this.height, this.width, this.height);
            this.mouseDragged(mc, mouseX, mouseY);
            int j = 14737632;
            if (packedFGColour != 0) {
                j = packedFGColour;
            } else if (!this.enabled) {
                j = 10526880;
            } else if (this.hovered) {
                j = 16777120;
            }

            this.drawCenteredString(fontrenderer, this.displayString, this.x + this.width / 2, this.y + (this.height - 8) / 2, j);
        }
    }
}

