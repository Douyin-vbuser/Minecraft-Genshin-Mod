package com.vbuser.inventory.packet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("all")
public class PacketPickUp implements IMessage {

    public String uuid,tab,item;

    public int count;

    public PacketPickUp(){}

    public PacketPickUp(String uuid,String tab,String item,int count){
        this.count = count;
        this.tab =tab;
        this.uuid = uuid;
        this.item = item;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        this.uuid = packetBuffer.readString(114);
        this.tab = packetBuffer.readString(114);
        this.item = packetBuffer.readString(114);
        this.count = packetBuffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeString(this.uuid);
        packetBuffer.writeString(this.tab);
        packetBuffer.writeString(this.item);
        packetBuffer.writeInt(this.count);
    }

    public static class HandlerPickUp implements IMessageHandler<PacketPickUp, IMessage> {

        @Override
        public IMessage onMessage(PacketPickUp message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            if(!inventoryFolder.exists()){
                inventoryFolder.mkdirs();
            }
            File inventoryFile = new File(inventoryFolder,message.tab+".json");

            try{

                Map<String, Map<String,Integer>> existingData = new HashMap<>();

                if(inventoryFile.exists()){
                    String existingJson = FileUtils.readFileToString(inventoryFile, StandardCharsets.UTF_8);
                    existingData = new Gson().fromJson(existingJson, new TypeToken<Map<String, Map<String,Integer>>>(){}.getType());
                }

                Map<String,Integer> newData = new HashMap<>();
                newData.put(message.item, message.count);

                if (!existingData.containsKey(message.uuid)) {
                    existingData.put(message.uuid, new HashMap<>());
                }

                Map<String, Integer> playerData = existingData.get(message.uuid);

                if (!playerData.containsKey(message.item)) {
                    playerData.put(message.item, message.count);
                } else {
                    playerData.put(message.item, playerData.get(message.item) + message.count);
                }
                existingData.put(message.uuid, playerData);


                FileWriter writer = new FileWriter(inventoryFile);
                String jsonOutput = new Gson().toJson(existingData);
                writer.write(jsonOutput);
                writer.close();

            }
            catch(Exception e){
                throw new RuntimeException(e);
            }

            return null;
        }
    }
}
