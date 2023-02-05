package com.vbuser.genshin.gui;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.network.PacketInventaireServer;
import com.vbuser.genshin.util.Reference;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
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

    public GuiCookMain(Minecraft mc){
        this.minecraft=mc;
    }

    public void initGui(){
        Main.network.sendToServer(new PacketInventaireServer());
        guiLeft = (this.width - this.xSize) / 2;
        guiTop = (this.height - this.ySize) / 2;
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