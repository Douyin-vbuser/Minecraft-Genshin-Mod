package com.vbuser.genshin.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class YangChengCaiLiao extends CreativeTabs {
    public YangChengCaiLiao(){
        super("yang_cheng_cai_liao");
    }

    @Override
    public ItemStack getTabIconItem(){
        return new ItemStack(Items.DIAMOND_SWORD);
    }
}
