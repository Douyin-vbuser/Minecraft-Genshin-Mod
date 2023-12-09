package com.vbuser.genshin.tabs;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class TabBase extends CreativeTabs {

    private final Item item;

    public TabBase(String label, Item item) {
        super(label);
        this.item = item;
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(item);
    }
}
