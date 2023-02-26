package com.vbuser.genshin.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ShiWuTab extends CreativeTabs {
    public ShiWuTab(){
        super("shiwu");
    }  //食物 food(specially designed for the Mod)

    @Override
    public ItemStack getTabIconItem(){
        return new ItemStack(Item.getItemFromBlock(Blocks.CAKE));
    }
}
