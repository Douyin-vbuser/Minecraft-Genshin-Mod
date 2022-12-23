package com.vbuser.genshin.init;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.items.ItemBase;
import net.minecraft.item.Item;

import java.util.ArrayList;
import java.util.List;

public class ModItems {
    public static final List<Item> ITEMS = new ArrayList<Item>();

    public static final Item XIANG_YU_ZHI_YUAN = new ItemBase("xiang_yu_zhi_yuan", Main.ZHENGGUIWUPING_TAB);
    //相遇之缘 Acquaint Fate

    public static final Item JIU_CHAN_ZHI_YUAN = new ItemBase("jiu_chan_zhi_yuan",Main.ZHENGGUIWUPING_TAB);
    //纠缠之缘 Intertwined Fate

    public static final Item SHUMEI = new ItemBase("shu_mei",Main.ZIRANCAILIAO);
    //树莓 Berry

    public static final Item LUOLUOMEI = new ItemBase("luoluomei",Main.ZIRANCAILIAO);
    //落落莓 Valberry

    public static final Item DEBUG_STICK = new ItemBase("debug_stick",Main.ZHENGGUIWUPING_TAB);
    //调试棒 Debug_stick
}
