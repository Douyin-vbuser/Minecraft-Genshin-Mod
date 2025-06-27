package com.vbuser.genshin.tabs;

import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

@SuppressWarnings("all")
public class TabBase extends CreativeTabs {

    private final Item item;

    public TabBase(String label, Item item) {
        super(label);
        this.item = item;
    }

    public TabBase(String label, Block block) {
        super(label);
        this.item = Item.getItemFromBlock(block);
    }

    @Override
    public ItemStack getTabIconItem() {
        return new ItemStack(item);
    }
}
