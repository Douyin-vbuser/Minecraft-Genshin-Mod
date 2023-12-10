package com.vbuser.inventory.packet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vbuser.inventory.event.PickItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class PacketArtifactAdd implements IMessage {

    String player_id,item_id;

    public PacketArtifactAdd(){}

    public PacketArtifactAdd(String player_id,String item_id,String item_name){
        this.player_id = player_id;
        this.item_id = item_name+":"+item_id;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        player_id = buffer.readString(114);
        item_id = buffer.readString(114);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(player_id);
        buffer.writeString(item_id);
    }

    public static class PacketArtifactAddHandler implements IMessageHandler<PacketArtifactAdd, IMessage>{

        @Override
        public IMessage onMessage(PacketArtifactAdd message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            if(!inventoryFolder.exists()){
                inventoryFolder.mkdirs();
            }
            File inventoryFile = new File(inventoryFolder,"sheng_yi_wu.json");

            try{
                Map<String,Map<String, Map<String,Integer>>> existingData = new HashMap<>();
                if(inventoryFile.exists()){
                    String existingJson = FileUtils.readFileToString(inventoryFile, StandardCharsets.UTF_8);
                    existingData = new Gson().fromJson(existingJson,new TypeToken<Map<String,Map<String, Map<String,Integer>>>>(){}.getType());
                }

                Map<String,Map<String,Integer>> newData = new HashMap<>();
                newData.put(message.item_id,new HashMap<>());

                if(!existingData.containsKey(message.player_id)){
                    existingData.put(message.player_id,newData);
                }
                else{
                    existingData.get(message.player_id).put(message.item_id,new HashMap<>());
                }

                FileWriter writer = new FileWriter(inventoryFile);
                String jsonOutput = new Gson().toJson(existingData);
                writer.write(jsonOutput);
                writer.close();
                PickItem.IODone = true;
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }

            return null;
        }
    }
}
