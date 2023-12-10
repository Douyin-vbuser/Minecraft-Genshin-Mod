package com.vbuser.inventory.packet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vbuser.inventory.CustomInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

public class PacketGetArtifact implements IMessage {

    String uuid;

    public PacketGetArtifact(){}

    public PacketGetArtifact(String uuid){
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.uuid = buffer.readString(114);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(this.uuid);
    }

    public static class PacketGetArtifactHandler implements IMessageHandler<PacketGetArtifact,IMessage>{
        @Override
        public IMessage onMessage(PacketGetArtifact message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            if(!inventoryFolder.exists()){
                return null;
            }
            File inventoryFile = new File(inventoryFolder,"sheng_yi_wu.json");

            if(inventoryFile.exists()){
                try{
                    String filePath =inventoryFile.getAbsolutePath();
                    FileReader reader = new FileReader(filePath);
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(reader);
                    Gson gson = new Gson();
                    Map<String, Map<String,Map<String,Integer>>> data = gson.fromJson(jsonElement, new TypeToken<Map<String, Map<String, Map<String,Integer>>>>(){}.getType());

                    CustomInventory.temp_1 = data.get(message.uuid);
                    CustomInventory.loadPacket = true;
                }
                catch (IOException e){
                    throw new RuntimeException(e);
                }
            }
            return null;
        }
    }
}
