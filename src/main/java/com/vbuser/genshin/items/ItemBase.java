package com.vbuser.genshin.items;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.init.ModItems;
import com.vbuser.genshin.util.IHasModel;
import net.minecraft.item.Item;

public class ItemBase extends Item implements IHasModel {

    public ItemBase(String name) {
        setUnlocalizedName(name);
        setRegistryName(name);
        ModItems.ITEMS.add(this);
    }

    @Override
    public void registerModels() {
        Main.proxy.registerItemRenderer(this, 0, "inventory");
    }
}
