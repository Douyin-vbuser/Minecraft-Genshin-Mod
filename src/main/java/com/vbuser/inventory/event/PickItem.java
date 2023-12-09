package com.vbuser.inventory.event;

import com.vbuser.inventory.CustomInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

public class PickItem {

    @SubscribeEvent
    public void onPlayPickItem(PlayerEvent.ItemPickupEvent event){
        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        int count = stack.getCount();
        CreativeTabs tabs = item.getCreativeTab();
        InventoryPlayer inventory = event.player.inventory;
        if(tabs!=null){
            inventory.clearMatchingItems(item,0, count,null);
            CustomInventory.addItem(event.player.getUniqueID(),stack);
        }
    }
}
