package com.vbuser.genshin.tab;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class JianCaiTab extends CreativeTabs {
    public JianCaiTab(){
        super("jiancai");   //建筑材料  Building Materials
    }

    @Override
    public ItemStack getTabIconItem(){
        return new ItemStack(Blocks.GRASS);
    }
}
