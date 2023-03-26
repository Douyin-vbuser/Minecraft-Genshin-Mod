package com.vbuser.genshin.gui;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.network.PacketInventaireServer;
import com.vbuser.genshin.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;

/**烹饪系统的实现参考了Titouan-Schotte的MMoCook(未声明开源协议)
 *为什么这个项目使用了极其诡异的GUI直接发包服务端从而绕开了界面容器(Container)????
 */
public class GuiCookMain extends GuiScreen {

    private final int xSize = 425;
    private final int ySize = 238;
    private int guiLeft;
    private int guiTop;
    private final Minecraft minecraft;
    public static int MoGuCount,QingRouCount,NiaoDanCount,ShouRouCount,LuoBoCount,BoHeCount,JueYunJiaoJiaoCount,
            TianTianHuaCount,JuanXingCaiCount, PingGuoCount,TuDouCount,NiuNaiCount,TangCount,XingRengCount,
            SongRongCount;

    public GuiCookMain(Minecraft mc){
        this.minecraft=mc;
    }

    public void initGui(){
        Main.network.sendToServer(new PacketInventaireServer());
        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
        buttonList.add(new GuiTexturedButton(0, guiLeft+47,guiTop+62, 46,46, "textures/items/truite/truite_bad.png", "textures/items/truite/truite_good.png", "textures/items/truite/truite_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(1, guiLeft+116,guiTop+61, 46,46, "textures/items/steak/steak_bad.png", "textures/items/steak/steak_good.png", "textures/items/steak/steak_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(2, guiLeft+198,guiTop+65, 35,35, "textures/items/omelet/omelet_bad.png", "textures/items/omelet/omelet_good.png", "textures/items/omelet/omelet_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(3, guiLeft+267,guiTop+60, 46,46, "textures/items/greensoupe/greensoupe_bad.png", "textures/items/greensoupe/greensoupe_good.png", "textures/items/greensoupe/greensoupe_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(4, guiLeft+338,guiTop+60, 46,46, "textures/items/confiture/confiture_bad.png", "textures/items/confiture/confiture_good.png", "textures/items/confiture/confiture_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(5, guiLeft+46,guiTop+125, 46,46, "textures/items/brochette_viande/brochette_viande_bad.png", "textures/items/brochette_viande/brochette_viande_good.png", "textures/items/brochette_viande/brochette_viande_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(6, guiLeft+116,guiTop+121, 46,46, "textures/items/brochette_champi/brochette_champi_bad.png", "textures/items/brochette_champi/brochette_champi_good.png", "textures/items/brochette_champi/brochette_champi_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(7, guiLeft+195,guiTop+125, 40,40, "textures/items/fishsoupe/fishsoupe_bad.png", "textures/items/fishsoupe/fishsoupe_good.png", "textures/items/fishsoupe/fishsoupe_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(8, guiLeft+272,guiTop+127, 38,38, "textures/items/cake/cake_bad.png", "textures/items/cake/cake_good.png", "textures/items/cake/cake_locked.png",0,0));
        buttonList.add(new GuiTexturedButton(9, guiLeft+338,guiTop+124, 46,46, "textures/items/cookie/cookie_bad.png", "textures/items/cookie/cookie_good.png", "textures/items/cookie/cookie_locked.png",0,0));
        buttonList.add(new GuiCustomButton(10, guiLeft + 165, guiTop + 185, 100, 20, I18n.format("cook"), 0, 0));
        buttonList.add(new GuiCustomButton(11, guiLeft + 396, guiTop+20, 18, 20, "X", 0, 0));
        buttonList.add(new GuiCustomButton(12, guiLeft + 100, guiTop+185, 18, 20, "-", 0, 0));
        buttonList.add(new GuiCustomButton(13, guiLeft + 118, guiTop+185, 18, 20, "1", 20, 0));
        buttonList.add(new GuiCustomButton(14, guiLeft + 136, guiTop+185, 18, 20, "+", 82, 0));
    }

    @Override
    public void updateScreen() {
        super.updateScreen();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        minecraft.getTextureManager().bindTexture(new ResourceLocation(Reference.Mod_ID,"textures/gui/cook/main.png"));
        drawModalRectWithCustomSizedTexture(guiLeft, guiTop, 0, 0, xSize, ySize, xSize, ySize);
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}