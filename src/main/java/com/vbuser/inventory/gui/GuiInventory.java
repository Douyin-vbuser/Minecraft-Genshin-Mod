package com.vbuser.inventory.gui;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.items.ShengYiWuBase;
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
    private Map<String, Map<String,Integer>> data_1;
    private Map<String,String> data_2;
    private final List<CreativeTabs> tabList;
    private int selectedTabIndex = 0;
    private int scrollIndex = 0;
    private int visibleItems;
    private String selectedItemName;
    private String selectedItemUUID;

    public GuiInventory() {
        tabList = new ArrayList<>();
        for (CreativeTabs tab : Main.TABS) {
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
        if(tabList.get(selectedTabIndex)==Main.SHENG_YI_WU){
            if(CustomInventory.temp_1==null){
                data_1 = new HashMap<>();
            }
            else {
                data_1 = CustomInventory.getItem1(Minecraft.getMinecraft().player.getUniqueID());
            }
        }
        else{
            if(tabList.get(selectedTabIndex)==Main.WU_QI){
                if(CustomInventory.temp_2==null){
                    data_2 = new HashMap<>();
                }
                else {
                    data_2 = CustomInventory.getItem2(Minecraft.getMinecraft().player.getUniqueID());
                }
            }
            else {
                data = CustomInventory.getItem(Minecraft.getMinecraft().player.getUniqueID(), getSelectedTab().getTabLabel());
            }
            }
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
        if(tabList.get(selectedTabIndex)!=Main.SHENG_YI_WU) {
            if(tabList.get(selectedTabIndex)==Main.WU_QI){
                if(data_2 != CustomInventory.temp_2){
                    if(CustomInventory.temp_2 == null){
                        data_2 = new HashMap<>();
                    }
                    else {
                        data_2 = CustomInventory.getItem2(Minecraft.getMinecraft().player.getUniqueID());
                    }
                }
                updateVisibleItems();
                drawWeaponList(visibleItems);
                super.drawScreen(mouseX, mouseY, partialTicks);
                drawWeaponTooltip(selectedItemName, mouseX, mouseY);
            }
            else {
                if (data != CustomInventory.temp) {
                    data = CustomInventory.getItem(Minecraft.getMinecraft().player.getUniqueID(), getSelectedTab().getTabLabel());
                }
                updateVisibleItems();
                drawItemList(visibleItems);
                super.drawScreen(mouseX, mouseY, partialTicks);
                drawItemTooltip(selectedItemName, mouseX, mouseY);
            }
        }
        else{
            if(data_1 != CustomInventory.temp_1){
                if(CustomInventory.temp_1 == null){
                    data_1 = new HashMap<>();
                }
                else {
                    data_1 = CustomInventory.getItem1(Minecraft.getMinecraft().player.getUniqueID());
                }
            }
            updateVisibleItems();
            drawArtifactList(visibleItems);
            super.drawScreen(mouseX, mouseY, partialTicks);
            drawArtifactTooltip(selectedItemUUID, mouseX, mouseY);
        }
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
                String item = "genshin:"+itemName.split("\\.")[1];
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

    private void drawWeaponList(int listCount){
        int yPos = 60;
        int count = 0;
        int lineHeight = 20;

        if(data_2 == null) {
            if (CustomInventory.temp_2 == null) {
                data_2 = new HashMap<>();
            } else {
                data_2 = CustomInventory.getItem2(Minecraft.getMinecraft().player.getUniqueID());
            }
        }
        for (Map.Entry<String, String> entry : data_2.entrySet()) {
            if (count >= scrollIndex && count < scrollIndex + listCount) {
                String itemName = entry.getKey();
                String item = "genshin:"+itemName.split(":")[0];
                if(Item.getByNameOrId(item)==null)continue;
                ItemStack itemStack = new ItemStack(Objects.requireNonNull(Item.getByNameOrId(item)));

                int itemX = (this.width / 2 - 50);
                int iconX = itemX - 20;

                RenderHelper.enableGUIStandardItemLighting();
                this.itemRender.renderItemAndEffectIntoGUI(itemStack, iconX, yPos);
                RenderHelper.disableStandardItemLighting();

                this.fontRenderer.drawString(itemStack.getDisplayName(), itemX, yPos, 0xFFFFFF);
                yPos += lineHeight;
            }
            count++;
        }
    }

    private void drawArtifactList(int listCount){
        int yPos = 60;
        int count = 0;
        int lineHeight = 20;

        for (Map.Entry<String, Map<String,Integer>> entry : data_1.entrySet()) {
            if (count >= scrollIndex && count < scrollIndex + listCount) {
                String itemName = entry.getKey();
                String item = "genshin:"+itemName.split(":")[0];
                if(Item.getByNameOrId(item)==null)continue;
                ItemStack itemStack = new ItemStack(Objects.requireNonNull(Item.getByNameOrId(item)));

                int itemX = (this.width / 2 - 50);
                int iconX = itemX - 20;

                RenderHelper.enableGUIStandardItemLighting();
                this.itemRender.renderItemAndEffectIntoGUI(itemStack, iconX, yPos);
                RenderHelper.disableStandardItemLighting();

                this.fontRenderer.drawString(itemStack.getDisplayName(), itemX, yPos, 0xFFFFFF);
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
                        if(tabList.get(selectedTabIndex)!=Main.SHENG_YI_WU) {
                            if(tabList.get(selectedTabIndex)==Main.WU_QI){
                                if(data_2 == null){
                                    data_2 = new HashMap<>();
                                }
                                else {
                                    data_2 = CustomInventory.getItem2(Minecraft.getMinecraft().player.getUniqueID());
                                }
                            }
                            else {
                                data = CustomInventory.getItem(Minecraft.getMinecraft().player.getUniqueID(), getSelectedTab().getTabLabel());
                            }
                        }
                        else{
                            if(CustomInventory.temp_1 == null){
                                data_1 = new HashMap<>();
                            }
                            else {
                                data_1 = CustomInventory.getItem1(Minecraft.getMinecraft().player.getUniqueID());
                            }
                        }
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

        assert tabList != null;
        if(tabList.get(selectedTabIndex)!=Main.SHENG_YI_WU) {
            if(tabList.get(selectedTabIndex)==Main.WU_QI){
                if(data_2== null){data_2 = new HashMap<>();}
                for(Map.Entry<String,String> entry : data_2.entrySet()){
                    if (count >= scrollIndex && count < scrollIndex + visibleItems) {
                        if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
                            selectedItemUUID = entry.getKey();
                            break;
                        }
                        yPos += lineHeight;
                    }
                    count++;
                }
            }
            else {
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
        }
        else{
            for (Map.Entry<String, Map<String,Integer>> entry : data_1.entrySet()) {
                if (count >= scrollIndex && count < scrollIndex + visibleItems) {
                    if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
                        selectedItemUUID = entry.getKey();
                        break;
                    }
                    yPos += lineHeight;
                }
                count++;
            }
        }
    }


    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();

        int delta = org.lwjgl.input.Mouse.getEventDWheel();
        if (delta != 0) {
            int maxScroll;
            if(tabList.get(selectedTabIndex) != Main.SHENG_YI_WU) {
                if(tabList.get(selectedTabIndex) == Main.WU_QI){
                    maxScroll = Math.max(0, data_2.size() - 5);
                }
                else {
                    maxScroll = Math.max(0, data.size() - 5);
                }
            }
            else{
                maxScroll = Math.max(0, data_1.size() - 5);
            }
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
                            if(tabList.get(selectedTabIndex)!=Main.SHENG_YI_WU){
                                drawHoveringText(Collections.singletonList(I18n.format(itemName+".description")), mouseX, mouseY);
                            }
                            break;
                        }
                    }
                    yPos += lineHeight;
                }
                count++;
            }
        }
    }

    private void drawArtifactTooltip(String uuid, int mouseX, int mouseY) {
        if (uuid != null && !uuid.isEmpty()) {
            int iconX = (this.width / 2 - 50) - 20;
            int yPos = 60;
            int lineHeight = 20;
            int count = 0;

            for (Map.Entry<String, Map<String,Integer>> entry : data_1.entrySet()) {
                if (count >= scrollIndex && count < scrollIndex + visibleItems) {
                    if(uuid.equals(entry.getKey())) {
                        if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
                            if (tabList.get(selectedTabIndex) == Main.SHENG_YI_WU) {
                                Map<String, Integer> itemData = data_1.get(uuid);
                                if (itemData != null) {
                                    List<String> tooltipLines = getStrings(itemData);
                                    drawHoveringText(tooltipLines, mouseX, mouseY);
                                }
                            }
                            break;
                        }
                    }

                    yPos += lineHeight;
                }
                count++;
            }
        }
    }

    private void drawWeaponTooltip(String uuid, int mouseX, int mouseY) {
        if (uuid != null && !uuid.isEmpty()) {
            int iconX = (this.width / 2 - 50) - 20;
            int yPos = 60;
            int lineHeight = 20;
            int count = 0;

            for (Map.Entry<String, String> entry : data_2.entrySet()) {
                if (count >= scrollIndex && count < scrollIndex + visibleItems) {
                    if(uuid.equals(entry.getKey())) {
                        if (mouseX >= iconX && mouseX <= iconX + 16 && mouseY >= yPos && mouseY <= yPos + 16) {
                            if (tabList.get(selectedTabIndex) == Main.WU_QI) {
                                    String itemData = data_2.get(uuid);
                                    drawHoveringText(I18n.format("genshin.dj")+":"+itemData.split(",")[0]+" "+I18n.format("genshin.jy")+":"+itemData.split(",")[1], mouseX, mouseY);
                                }
                            }
                            break;
                        }
                    yPos += lineHeight;
                }
                count++;
            }
        }
    }

    private static List<String> getStrings(Map<String, Integer> itemData) {
        Map<String, StringBuilder> propertyMap = new HashMap<>();
        List<String> tooltipLines = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : itemData.entrySet()) {
            String key = entry.getKey();
            Integer value = entry.getValue();

            if (!key.endsWith("Property") && !key.endsWith("Value")) {
                continue;
            }

            String prefix = key.substring(0, key.length() - (key.endsWith("Property")?9:6));

            if (key.endsWith("Property")) {
                propertyMap.put(prefix, Optional.of(I18n.format("genshin."+ShengYiWuBase.translateProperty(value)))
                        .map(StringBuilder::new)
                        .orElse(null));
            }

            if (key.endsWith("Value")) {
                StringBuilder temp = propertyMap.get(prefix);
                temp = (temp == null ? new StringBuilder("null") : temp).append(":").append(value);
                tooltipLines.add(temp.toString());
            }
        }

        return tooltipLines;
    }

    private CreativeTabs getSelectedTab() {
        return tabList.get(selectedTabIndex);
    }
}