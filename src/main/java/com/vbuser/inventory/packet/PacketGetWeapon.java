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

public class PacketGetWeapon implements IMessage {

    String uuid;

    public PacketGetWeapon(){}

    public PacketGetWeapon(String uuid){
        this.uuid = uuid;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        this.uuid = packetBuffer.readString(114);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.writeString(this.uuid);
    }

    public static class PacketGetWeaponHandler implements IMessageHandler<PacketGetWeapon,IMessage>{

        @Override
        public IMessage onMessage(PacketGetWeapon message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            if(!inventoryFolder.exists()){
                return null;
            }
            File inventoryFile = new File(inventoryFolder,"wu_qi.json");

            if(inventoryFile.exists()){
                try{
                    String filePath =inventoryFile.getAbsolutePath();
                    FileReader reader = new FileReader(filePath);
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(reader);
                    Gson gson = new Gson();
                    Map<String, Map<String, String>> data = gson.fromJson(jsonElement, new TypeToken<Map<String, Map<String, String>>>(){}.getType());

                    CustomInventory.temp_2 = data.get(message.uuid);
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
