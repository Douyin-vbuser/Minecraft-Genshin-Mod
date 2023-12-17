package com.vbuser.inventory.event;

import com.vbuser.genshin.Main;
import com.vbuser.genshin.tabs.TabBase;
import com.vbuser.inventory.CustomInventory;
import com.vbuser.inventory.packet.PacketAddWeapon;
import com.vbuser.inventory.packet.PacketArtifactAdd;
import com.vbuser.inventory.packet.PacketArtifactNBT;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.util.UUID;

public class PickItem {

    public static boolean IODone = false;

    @SubscribeEvent
    public void onPlayPickItem(PlayerEvent.ItemPickupEvent event){
        ItemStack stack = event.getStack();
        Item item = stack.getItem();
        int count = stack.getCount();
        CreativeTabs tabs = item.getCreativeTab();
        String item_name = item.getUnlocalizedName().split("\\.")[1];
        InventoryPlayer inventory = event.player.inventory;
        if(tabs instanceof TabBase){
            inventory.clearMatchingItems(item,0, count,stack.getTagCompound());
            if(tabs == Main.SHENG_YI_WU){
                UUID item_id = UUID.randomUUID();
                CustomInventory.network.sendToServer(new PacketArtifactAdd(event.player.getUniqueID().toString(),item_id.toString(),item_name));
                if (stack.hasTagCompound()) {
                    NBTTagCompound compound = stack.getTagCompound();
                    if (compound != null) {
                        for (String key : compound.getKeySet()) {
                            while(!IODone){
                                try {
                                    Thread.sleep(5);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                            IODone = false;
                            CustomInventory.network.sendToServer(new PacketArtifactNBT(event.player.getUniqueID().toString(),item_name+":"+item_id,key,Integer.parseInt(compound.getTag(key).toString())));
                        }
                    }
                }
            }
            else{
                if(tabs == Main.WU_QI){
                    UUID item_id = UUID.randomUUID();
                    CustomInventory.network.sendToServer(new PacketAddWeapon(event.player.getUniqueID().toString(),item_id.toString(),item_name,0,1));
                }
                else {
                    CustomInventory.addItem(event.player.getUniqueID(), stack);
                }
            }
        }
    }
}
