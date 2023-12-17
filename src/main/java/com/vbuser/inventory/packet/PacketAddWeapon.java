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
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class PacketAddWeapon implements IMessage {

    String player,uuid,name;
    int xp,level;

    public PacketAddWeapon(){}

    public PacketAddWeapon(String player,String uuid, String name, int xp, int level){
        this.player = player;
        this.uuid = uuid;
        this.name = name;
        this.xp = xp;
        this.level = level;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        this.player = packetBuffer.readString(114);
        this.uuid = packetBuffer.readString(114);
        this.name = packetBuffer.readString(114);
        this.xp = packetBuffer.readInt();
        this.level = packetBuffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeString(this.player);
        packetBuffer.writeString(this.uuid);
        packetBuffer.writeString(this.name);
        packetBuffer.writeInt(this.xp);
        packetBuffer.writeInt(this.level);
    }

    public static class PacketWeaponHandler implements IMessageHandler<PacketAddWeapon,IMessage> {
        @Override
        public IMessage onMessage(PacketAddWeapon message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            if(!inventoryFolder.exists()){
                inventoryFolder.mkdirs();
            }
            File inventoryFile = new File(inventoryFolder,"wu_qi.json");

            try{
                Map<String,Map<String,String>> existingData = new HashMap<>();
                if(inventoryFile.exists()){
                    String existingJson = FileUtils.readFileToString(inventoryFile, StandardCharsets.UTF_8);
                    existingData = new Gson().fromJson(existingJson,new TypeToken<Map<String,Map<String, String>>>(){}.getType());
                }

                Map<String,String> newData = new HashMap<>();
                newData.put(message.name+":"+message.uuid,message.level+":"+ message.xp);

                if(!existingData.containsKey(message.player)){
                    existingData.put(message.player,newData);
                }
                else{
                    Map<String,String> existingPlayerData = existingData.get(message.player);
                    existingPlayerData.put(message.name+":"+message.uuid,message.level+":"+ message.xp);
                    existingData.put(message.player,existingPlayerData);
                }

                FileWriter writer = new FileWriter(inventoryFile);
                String jsonOutput = new Gson().toJson(existingData);
                writer.write(jsonOutput);
                writer.close();
            }
            catch (IOException e){
                throw new RuntimeException(e);
            }

            return null;
        }
    }
}
