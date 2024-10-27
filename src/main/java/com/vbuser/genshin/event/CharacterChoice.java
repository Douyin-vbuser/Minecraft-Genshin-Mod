package com.vbuser.genshin.event;

import com.vbuser.database.operate.Console;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CharacterChoice {

    private static final Map<UUID, Map<Integer,Integer>> character = new HashMap<>(10);
    //-1 means the slot is not appointed yet.

    private static final Map<UUID,Integer> choice = new HashMap<>(10);

    public static Map<UUID,Map<Integer,Integer>> get(){
        return character;
    }

    public static int getChoice(UUID player){
        return choice.get(player);
    }

    public static void set(UUID player,int slot,int character){
        Map<Integer,Integer> temp = get().get(player);
        temp.put(slot,character);
    }

    File worldDirectory;

    @SubscribeEvent
    public void playerEnterWorld(PlayerEvent.PlayerLoggedInEvent event) throws IOException {
        EntityPlayer player = event.player;
        if(player instanceof EntityPlayerMP){
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            worldDirectory = playerMP.getServerWorld().getSaveHandler().getWorldDirectory();
        }

        File database = new File(worldDirectory,"genshin_data");
        if(!database.exists()){
            Console.executeCommand("init genshin_data "+worldDirectory.getAbsolutePath());
        }
        Console.executeCommand("access "+database.getAbsolutePath());
        File table = new File(new File(database,"tables"),"char_list.txt");
        if(!table.exists()){
            Console.executeCommand("create table char_list (uuid,1,2,3,4)");
        }
        String[] data = Console.getResult("select * from char_list where uuid="+event.player.getUniqueID());
        if(data.length==0){
            Map<Integer,Integer> temp = new HashMap<>(4);
            temp.put(1,0);temp.put(2,-1);temp.put(3,-1);temp.put(4,-1);
            character.put(event.player.getUniqueID(),temp);
        }else{
            String[] player_data = data[0].split(">");
            Map<Integer,Integer> temp = new HashMap<>(4);
            temp.put(1,Integer.parseInt(player_data[1]));temp.put(2,Integer.parseInt(player_data[2]));temp.put(3,Integer.parseInt(player_data[3]));temp.put(4,Integer.parseInt(player_data[4]));
            character.put(event.player.getUniqueID(),temp);
        }
        choice.put(player.getUniqueID(),1);
    }

    @SubscribeEvent
    public void playerLeaveWorld(PlayerEvent.PlayerLoggedOutEvent event) throws IOException {
        EntityPlayer player = event.player;
        if(player instanceof EntityPlayerMP){
            EntityPlayerMP playerMP = (EntityPlayerMP) player;
            worldDirectory = playerMP.getServerWorld().getSaveHandler().getWorldDirectory();
        }

        File database = new File(worldDirectory,"genshin_data");
        if(!database.exists()){
            Console.executeCommand("init genshin_data "+worldDirectory.getAbsolutePath());
        }
        Console.executeCommand("access "+database.getAbsolutePath());
        File table = new File(new File(database,"tables"),"char_list.txt");
        if(!table.exists()){
            Console.executeCommand("create table char_list (uuid,1,2,3,4)");
        }
        String[] data = Console.getResult("select * from char_list where uuid="+event.player.getUniqueID());
        if(data.length!=0){
            Console.executeCommand("delete from char_list where uuid="+event.player.getUniqueID());
        }
        UUID uuid = event.player.getUniqueID();
        String result = uuid+","+character.get(uuid).get(1)+","+character.get(uuid).get(2)+","+character.get(uuid).get(3)+","+character.get(uuid).get(4);
        String command = "insert into char_list (uuid,1,2,3,4) values ("+ result +")";
        Console.executeCommand(command);
        choice.remove(player.getUniqueID());
    }
}
