package com.vbuser.genshin.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.PotionTypes;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionBase extends Potion {
    double a = 0.00390625;
    protected static final ResourceLocation resource = new ResourceLocation("genshin", "textures/misc/potions.png");
    protected final int iconIndex;
    PotionType potionType = PotionTypes.MUNDANE;

    public PotionType getPotionType() {
        return potionType;
    }

    public PotionBase(boolean isBadEffectIn, int liquidColorIn, String name, int icon) {
        super(isBadEffectIn, liquidColorIn);
        setRegistryName(new ResourceLocation("genshin", name));
        setPotionName("genshin.potion." + name);
        iconIndex = icon;
        ModPotions.INSTANCES.add(this);
    }

    @SideOnly(Side.CLIENT)
    protected void render(int x, int y, float alpha) {
        Minecraft.getMinecraft().renderEngine.bindTexture(resource);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buf = tessellator.getBuffer();
        buf.begin(7, DefaultVertexFormats.POSITION_TEX);
        GlStateManager.color(1, 1, 1, alpha);

        int textureX = iconIndex % 14 * 18;
        int textureY = 198 - iconIndex / 14 * 18;

        buf.pos(x, y + 18, 0).tex(textureX * a, (textureY + 18) * a).endVertex();
        buf.pos(x + 18, y + 18, 0).tex((textureX + 18) * a, (textureY + 18) * a).endVertex();
        buf.pos(x + 18, y, 0).tex((textureX + 18) * 0.00390625, textureY * a).endVertex();
        buf.pos(x, y, 0).tex(textureX * a, textureY * a).endVertex();

        tessellator.draw();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderInventoryEffect(int x, int y, PotionEffect effect, Minecraft mc) {
        render(x + 6, y + 7, 1);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void renderHUDEffect(int x, int y, PotionEffect effect, Minecraft mc, float alpha) {
        render(x + 3, y + 3, alpha);
    }
}
