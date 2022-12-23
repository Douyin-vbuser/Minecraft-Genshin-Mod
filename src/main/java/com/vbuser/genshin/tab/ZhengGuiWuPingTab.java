package com.vbuser.genshin.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class ZhengGuiWuPingTab extends CreativeTabs {
    public ZhengGuiWuPingTab(){
        super("zhengguiwuping");  //珍贵物品 sth. important
    }

    @Override
    public ItemStack getTabIconItem(){
        return new ItemStack(Items.DIAMOND);
    }
}
