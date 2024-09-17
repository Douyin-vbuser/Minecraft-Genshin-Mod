package com.vbuser.database;

import com.vbuser.database.command.CommandDB;
import com.vbuser.database.command.CommandOperate;
import com.vbuser.database.event.PickUp;
import com.vbuser.database.operate.Console;
import com.vbuser.database.packet.OperateServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;
import java.util.UUID;

@Mod(modid = "database",name = "Database",version = "basic 1.0.0")
public class DataBase {

    @Mod.EventHandler
    public static void serverInit(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandDB());
        event.registerServerCommand(new CommandOperate());
    }

    public static SimpleNetworkWrapper network;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event){
        network = NetworkRegistry.INSTANCE.newSimpleChannel("database");
        network.registerMessage(OperateServer.Handler.class, OperateServer.class, 0, Side.SERVER);
    }

    @Mod.EventHandler
    public void onInit(FMLInitializationEvent event){
        MinecraftForge.EVENT_BUS.register(new PickUp());
    }

    //API to add common Item
    public static void addItem(Item item, int number, EntityPlayer player){
        assert item.getCreativeTab()!=null;
        String table = item.getCreativeTab().getTabLabel();
        String name = item.getUnlocalizedName().split("\\.")[1];
        File worldDirectory =((EntityPlayerMP) player).getServerWorld().getSaveHandler().getWorldDirectory();
        if(!new File(worldDirectory,"genshin_data").exists()){
            network.sendToServer(new OperateServer("init genshin_data "+worldDirectory.getAbsolutePath()));
        }
        if(!new File(worldDirectory,"genshin_data\\tables\\"+table+".txt").exists()){
            network.sendToServer(new OperateServer("create table "+table+" (player,item,number)"));
        }
        String[] temp = getContent("select * from "+table+" where player="+player.getUniqueID()+" and item="+name);
        if(temp.length==0){
            network.sendToServer(new OperateServer("insert into "+table+" (player,item,number) values ("+player.getUniqueID()+","+name+","+number+")"));
            network.sendToServer(new OperateServer("update "+table+" set number="+number+" where player="+player.getUniqueID()+" and item="+name));
        }else{
            int number_old = Integer.parseInt(temp[0].split(">")[2]);
            int result = number_old+number;
            if(result>0) {
                network.sendToServer(new OperateServer("update " + table + " set number=" + result + " where player=" + player.getUniqueID() + " and item=" + name));
            }else{
                if(result==0){
                    network.sendToServer(new OperateServer("delete from "+table+" where player="+player.getUniqueID()+" and item="+name));
                }else{
                    System.out.println("[!] Illegal operate.You can't subtract more than you have.");
                }
            }
            network.sendToServer(new OperateServer("update "+table+" set number="+result+" where player="+player.getUniqueID()+" and item="+name));
        }
    }

    //API to add Artifact:
    public static void addArtifact(ItemStack itemStack,EntityPlayer player){
        File worldDirectory =((EntityPlayerMP) player).getServerWorld().getSaveHandler().getWorldDirectory();
        if(!new File(worldDirectory,"genshin_data").exists()){
            network.sendToServer(new OperateServer("init genshin_data "+worldDirectory.getAbsolutePath()));
        }
        if(!new File(worldDirectory,"genshin_data\\tables\\sheng_yi_wu.txt").exists()){
            network.sendToServer(new OperateServer("create table sheng_yi_wu (player,item_uuid,state)"));
        }
        if(!new File(worldDirectory,"genshin_data\\tables\\artifacts.txt").exists()){
            network.sendToServer(new OperateServer("create table artifacts (item_id,jing_yan,deng_ji,main_property,main_value,aProperty,aValue,bProperty,bValue,cProperty,cValue,dProperty,dValue)"));
        }
        String item_uuid = UUID.randomUUID().toString();
        network.sendToServer(new OperateServer("insert into sheng_yi_wu (player,item_uuid,state) values ("+player.getUniqueID()+","+item_uuid+",0)"));
        String jing_yan,deng_ji,main_property,main_value,aProperty,aValue,bProperty,bValue,cProperty,cValue,dProperty,dValue;
        if(itemStack.getTagCompound()!=null){
            NBTTagCompound nbt = itemStack.getTagCompound();
            jing_yan = nbt.hasKey("jing_yan") ? String.valueOf(nbt.getInteger("jing_yan")) : "@";
            deng_ji = nbt.hasKey("deng_ji") ? String.valueOf(nbt.getInteger("deng_ji")) : "@";
            main_property = nbt.hasKey("mainProperty") ? String.valueOf(nbt.getInteger("main_property")) : "@";
            main_value = nbt.hasKey("mainValue") ? String.valueOf(nbt.getInteger("main_value")) : "@";
            aProperty = nbt.hasKey("aProperty") ? String.valueOf(nbt.getInteger("aProperty")) : "@";
            aValue = nbt.hasKey("aValue") ? String.valueOf(nbt.getInteger("aValue")) : "@";
            bProperty = nbt.hasKey("bProperty") ? String.valueOf(nbt.getInteger("bProperty")) : "@";
            bValue = nbt.hasKey("bValue") ? String.valueOf(nbt.getInteger("bValue")) : "@";
            cProperty = nbt.hasKey("cProperty") ? String.valueOf(nbt.getInteger("cProperty")) : "@";
            cValue = nbt.hasKey("cValue") ? String.valueOf(nbt.getInteger("cValue")) : "@";
            dProperty = nbt.hasKey("dProperty") ? String.valueOf(nbt.getInteger("dProperty")) : "@";
            dValue = nbt.hasKey("dValue") ? String.valueOf(nbt.getInteger("dValue")) : "@";
            String command = "insert into artifacts (item_id,jing_yan,deng_ji,main_property,main_value,aProperty,aValue,bProperty,bValue,cProperty,cValue,dProperty,dValue) values ("+item_uuid+","+jing_yan+","+deng_ji+","+main_property+","+main_value+","+aProperty+","+aValue+","+bProperty+","+bValue+","+cProperty+","+cValue+","+dProperty+","+dValue+")";
            network.sendToServer(new OperateServer(command));
        }
    }

    public static String[] getContent(String command){
        network.sendToServer(new OperateServer(command));
        try {
            return Console.getResult();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
