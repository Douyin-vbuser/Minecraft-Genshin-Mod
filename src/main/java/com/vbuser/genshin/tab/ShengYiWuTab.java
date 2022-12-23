package com.vbuser.genshin.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class ShengYiWuTab extends CreativeTabs {
    public ShengYiWuTab(){
        super("shengyiwu");
    }  //圣遗物 Artifact

    @Override
    public ItemStack getTabIconItem(){
        return new ItemStack(Blocks.LEAVES);
    }
}
