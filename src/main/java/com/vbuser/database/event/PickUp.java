package com.vbuser.database.event;

import com.vbuser.database.DataBase;
import com.vbuser.genshin.Main;
import com.vbuser.genshin.tabs.TabBase;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PickUp {
    @SubscribeEvent
    public void onPlayPickItem(PlayerEvent.ItemPickupEvent event) {
        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        int count = stack.getCount();
        CreativeTabs tabs = item.getCreativeTab();
        InventoryPlayer inventory = event.player.inventory;
        if (tabs instanceof TabBase) {
            inventory.clearMatchingItems(item, 0, count, stack.getTagCompound());
            if (tabs == Main.SHENG_YI_WU) {
                DataBase.addArtifact(stack, event.player);
            } else {
                DataBase.addItem(item, count, event.player);
            }
        }
    }
}
