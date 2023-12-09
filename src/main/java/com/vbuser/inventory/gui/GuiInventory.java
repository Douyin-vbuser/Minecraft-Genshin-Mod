package com.vbuser.inventory.gui;

import com.vbuser.inventory.CustomInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.lwjgl.input.Keyboard;

import java.io.IOException;
import java.util.*;

public class GuiInventory extends GuiScreen {
    private Map<String, Integer> data;
    private final List<CreativeTabs> tabList;
    private int selectedTabIndex = 0;
    private int scrollIndex = 0;
    private int visibleItems;
    private String selectedItemName;

    public GuiInventory() {
        tabList = new ArrayList<>(CreativeTabs.CREATIVE_TAB_ARRAY.length);
        for (CreativeTabs tab : CreativeTabs.CREATIVE_TAB_ARRAY) {
            if (tab != null) {
                tabList.add(tab);
            }
        }
        if(this.buttonList.isEmpty()){
        int buttonWidth = 60;
        int buttonHeight = 20;
        this.buttonList.add(new GuiButton(0, this.width / 2 - buttonWidth / 2, this.height - 30, buttonWidth, buttonHeight, "Close"));
        }
    }

    @Override
    public void initGui() {
        data = CustomInventory.getItem(Minecraft.getMinecraft().player.getUniqueID(), getSelectedTab().getTabLabel());
        if(this.buttonList.isEmpty()){
            int buttonWidth = 60;
            int buttonHeight = 20;
            this.buttonList.add(new GuiButton(0, this.width / 2 - buttonWidth / 2, this.height - 30, buttonWidth, buttonHeight, "Close"));
        }
        updateVisibleItems();
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawDefaultBackground();
        if(this.buttonList.isEmpty()){
            int buttonWidth = 60;
            int buttonHeight = 20;
            this.buttonList.add(new GuiButton(0, this.width / 2 - buttonWidth / 2, this.height - 30, buttonWidth, buttonHeight, "Close"));
        }
        drawTabs();
        drawSelectedTabInfo();
        if(data!=CustomInventory.temp){
            data = CustomInventory.getItem(Minecraft.getMinecraft().player.getUniqueID(), getSelectedTab().getTabLabel());
        }
        updateVisibleItems();
        drawItemList(visibleItems);
        super.drawScreen(mouseX, mouseY, partialTicks);
        drawItemTooltip(selectedItemName, mouseX, mouseY);
    }

    private void drawTabs() {
        int tabSize = 24;
        int x = this.width / 2 - (tabList.size() * tabSize) / 2;
        int y = 10;

        for (int i = 0; i < tabList.size(); i++) {
            CreativeTabs tab = tabList.get(i);
            ItemStack iconStack = tab.getTabIconItem();
            RenderHelper.enableGUIStandardItemLighting();
            this.itemRender.renderItemAndEffectIntoGUI(iconStack, x + i * tabSize, y);
            RenderHelper.disableStandardItemLighting();
        }
    }

    private void drawSelectedTabInfo() {
        CreativeTabs selectedTab = getSelectedTab();
        String tabName = I18n.format(selectedTab.getTranslatedTabLabel());
        this.fontRenderer.drawString(tabName, this.width / 2 - this.fontRenderer.getStringWidth(tabName) / 2, 30, 0xFFFFFF);

        this.buttonList.get(0).x = this.width / 2 - 30;
        this.buttonList.get(0).y = this.height - 30;
    }

    private void drawItemList(int listCount) {
        int yPos = 60;
        int count = 0;
        int lineHeight = 20;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (count >= scrollIndex && count < scrollIndex + listCount) {
                String itemName = entry.getKey();
                int itemCount = entry.getValue();
                String item = itemName.split("\\.")[1];
                if(Item.getByNameOrId(item)==null)continue;
                ItemStack itemStack = new ItemStack(Objects.requireNonNull(Item.getByNameOrId(item)));

                int itemX = (this.width / 2 - 50);
                int iconX = itemX - 20;

                RenderHelper.enableGUIStandardItemLighting();
                this.itemRender.renderItemAndEffectIntoGUI(itemStack, iconX, yPos);
                RenderHelper.disableStandardItemLighting();

                this.fontRenderer.drawString(itemStack.getDisplayName(), itemX, yPos, 0xFFFFFF);

                String itemCountString = String.valueOf(itemCount);
                int countX = itemX + this.fontRenderer.getStringWidth(itemStack.getDisplayName()) + 5;
                this.fontRenderer.drawString(itemCountString, countX, yPos, 0xFFFFFF);

                yPos += lineHeight;
            }
            count++;
        }
    }

    @Override
    protected void actionPerformed(GuiButton button){
        if (button.id == 0) {
            this.mc.displayGuiScreen(null);
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        if (tabList != null && !tabList.isEmpty()) {
            int tabSize = 24;
            int x = this.width / 2 - (tabList.size() * tabSize) / 2;
            int y = 10;

            if (selectedTabIndex >= 0 && selectedTabIndex < tabList.size()) {
                for (int i = 0; i < tabList.size(); i++) {
                    if (mouseX >= x + i * tabSize && mouseX <= x + (i + 1) * tabSize && mouseY >= y && mouseY <= y + tabSize) {
                        selectedTabIndex = i;
                        data = CustomInventory.getItem(Minecraft.getMinecraft().player.getUniqueID(), getSelectedTab().getTabLabel());
                        scrollIndex = 0;
                        break;
                    }
                }
            }
        }

        int iconX = (this.width / 2 - 50) - 20;
        int yPos = 60;
        int lineHeight = 20;
        int count = 0;

        for (Map.Entry<String, Integer> entry : data.entrySet()) {
            if (count >= scrollIndex && count < scrollIndex + visibleItems) {
                if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
                    selectedItemName = entry.getKey().split("\\.")[1];
                    break;
                }
                yPos += lineHeight;
            }
            count++;
        }
    }


    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int delta = org.lwjgl.input.Mouse.getEventDWheel();
        if (delta != 0) {
            int maxScroll = Math.max(0, data.size() - 5);
            scrollIndex = Math.max(0, Math.min(maxScroll, scrollIndex - Integer.signum(delta)));
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (keyCode == Keyboard.KEY_B || keyCode == Keyboard.KEY_ESCAPE) {
            Minecraft.getMinecraft().player.closeScreen();
        }
    }

    private void updateVisibleItems() {
        int availableSpace = this.height - 100;
        int lineHeight = 20;
        visibleItems = Math.max(0, availableSpace / lineHeight);
    }

    private void drawItemTooltip(String itemName, int mouseX, int mouseY) {
        if (itemName != null && !itemName.isEmpty()) {
            int iconX = (this.width / 2 - 50) - 20;
            int yPos = 60;
            int lineHeight = 20;
            int count = 0;

            for (Map.Entry<String, Integer> entry : data.entrySet()) {
                if (count >= scrollIndex && count < scrollIndex + visibleItems) {
                    if (itemName.equals(entry.getKey().split("\\.")[1])) {
                        if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
                            drawHoveringText(Collections.singletonList(I18n.format(itemName+".description")), mouseX, mouseY);
                            break;
                        }
                    }
                    yPos += lineHeight;
                }
                count++;
            }
        }
    }

    private CreativeTabs getSelectedTab() {
        return tabList.get(selectedTabIndex);
    }
}