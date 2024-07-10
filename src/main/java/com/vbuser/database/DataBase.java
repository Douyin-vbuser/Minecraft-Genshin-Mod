package com.vbuser.database;

import com.vbuser.database.command.CommandDB;
import com.vbuser.database.command.CommandOperate;
import com.vbuser.database.operate.Console;
import com.vbuser.database.packet.OperateServer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.io.File;

@Mod(modid = "database",version = "basic 1.0.0")
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
        String[] temp = getItems("select * from "+table+" where player="+player.getUniqueID()+" and item="+name);
        if(temp.length==0){
            network.sendToServer(new OperateServer("insert into "+table+" (player,item,number) values ("+player.getUniqueID()+","+name+","+number+")"));
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
        }
    }

    public static String[] getItems(String command){
        network.sendToServer(new OperateServer(command));
        try {
            return Console.getResult();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
