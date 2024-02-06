package com.vbuser.inventory;

import com.vbuser.inventory.command.CommandInventory;
import com.vbuser.inventory.event.BPress;
import com.vbuser.inventory.event.PickItem;
import com.vbuser.inventory.gui.ModGuiLoader;
import com.vbuser.inventory.key.KeyboardManager;
import com.vbuser.inventory.packet.*;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("all")
@Mod(modid = "inventory",version = "release 1.9.19")
public class CustomInventory {


    //Registry performances:

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new PickItem());
        MinecraftForge.EVENT_BUS.register(new BPress());
        new ModGuiLoader();
        KeyboardManager.init();
    }

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        network = NetworkRegistry.INSTANCE.newSimpleChannel("inventory_channel");
        network.registerMessage(PacketPickUp.HandlerPickUp.class, PacketPickUp.class, 0, Side.SERVER);
        network.registerMessage(PacketGetItem.PacketGetItemHandler.class, PacketGetItem.class, 1, Side.SERVER);
        network.registerMessage(PacketDecreaseItem.PacketDecreaseItemHandler.class, PacketDecreaseItem.class, 2, Side.SERVER);
        network.registerMessage(BPress.PacketInventoryGuiHandler.class,BPress.PacketInventoryGui.class,3,Side.SERVER);
        network.registerMessage(PacketArtifactAdd.PacketArtifactAddHandler.class, PacketArtifactAdd.class,4,Side.SERVER);
        network.registerMessage(PacketArtifactNBT.PacketArtifactNBTHandler.class, PacketArtifactNBT.class,5,Side.SERVER);
        network.registerMessage(PacketGetArtifact.PacketGetArtifactHandler.class,PacketGetArtifact.class,6,Side.SERVER);
        network.registerMessage(PacketGetWeapon.PacketGetWeaponHandler.class,PacketGetWeapon.class,7,Side.SERVER);
        network.registerMessage(PacketAddWeapon.PacketWeaponHandler.class,PacketAddWeapon.class,8,Side.SERVER);
    }

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandInventory());
    }

    @Mod.Instance
    public static CustomInventory instance;

    //APIs providing for inventory management:

    //APIs to add item:

    public static boolean addItem(UUID uuid, Item item, int count) {
        if (item.getCreativeTab() != null) {
            network.sendToServer(new PacketPickUp(uuid.toString(), item.getCreativeTab().getTabLabel(), item.getUnlocalizedName(), count));
            return true;
        } else return false;
    }

    public static boolean addItem(UUID uuid, ItemStack stack) {
        if (stack.getItem().getCreativeTab() != null) {
            network.sendToServer(new PacketPickUp(uuid.toString(), stack.getItem().getCreativeTab().getTabLabel(), stack.getItem().getUnlocalizedName(), stack.getCount()));
            return true;
        } else return false;
    }

    //APIs to get inventory:

    public static Map<String, Integer> temp = new HashMap<>();

    public static boolean loadPacket;

    //APIs designed for common items:
    public static Map<String, Integer> getItem(UUID uuid, String tab) {
        temp.clear();
        int tryTime = 0;
        loadPacket = false;
        network.sendToServer(new PacketGetItem(uuid.toString(), tab));
        while(!loadPacket && tryTime <= 20) {
            try {
                Thread.sleep(5);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            tryTime++;  //It usually takes about 50ms to get information from server.
        }
        return temp;
    }

    public static Map<String,Map<String,Integer>> temp_1 = new HashMap<>();

    //APIs designed for artifacts:
    public static Map<String,Map<String,Integer>> getItem1(UUID uuid) {
        temp_1.clear();
        int tryTime = 0;
        loadPacket = false;
        network.sendToServer(new PacketGetArtifact(uuid.toString()));
        while(!loadPacket && tryTime <= 20){
            try{
                Thread.sleep(5);
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            tryTime++;
        }
        return temp_1;
    }

    public static Map<String,String> temp_2 = new HashMap<>();

    public static Map<String,String> getItem2(UUID uuid) {
        if(temp_2 == null){
            temp_2 = new HashMap<>();
        }
        temp_2.clear();
        int tryTime = 0;
        loadPacket = false;
        network.sendToServer(new PacketGetWeapon(uuid.toString()));
        while(!loadPacket && tryTime <= 20){
            try{
                Thread.sleep(5);
            }
            catch (InterruptedException e){
                throw new RuntimeException(e);
            }
            tryTime++;
        }
        return temp_2;
    }

    //APIs to delete items:
    public static boolean deleteItem(UUID uuid, String item, int count) {
        Item item1 = Item.getByNameOrId(item);
        CreativeTabs tab = null;
        if (item1 != null) {
            tab = item1.getCreativeTab();
        }

        Map<String,Integer> data = getItem(uuid, tab != null ? tab.getTabLabel() : null);

        if (data.isEmpty() ||data.get(Item.getByNameOrId(item).getUnlocalizedName()) == null ) {
            return false;
        }else {
            if (data.get(Item.getByNameOrId(item).getUnlocalizedName()) >= count) {
                network.sendToServer(new PacketDecreaseItem(uuid.toString(), item, count));
                return true;
            } else {
                return false;
            }
        }
    }
}
