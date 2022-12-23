package com.vbuser.genshin.tab;

import com.vbuser.genshin.init.ModBlocks;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;

public class ZiRanCaiLiao extends CreativeTabs {
    public ZiRanCaiLiao(){
        super("zirancailiao");
    }  //自然材料  some simple items

    @Override
    public ItemStack getTabIconItem(){
        return new ItemStack(ModBlocks.TIAN_TIAN_HUA);
    }
}
