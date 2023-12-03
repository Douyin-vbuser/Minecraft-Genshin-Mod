package com.vbuser.genshin.inventory.packet;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.vbuser.genshin.inventory.CustomInventory;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

public class PacketGetItem implements IMessage {

    public UUID uuid;
    public String tab;

    public PacketGetItem() {}

    public PacketGetItem(String uuid,String tab){
        this.uuid = UUID.fromString(uuid);
        this.tab = tab;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.uuid = UUID.fromString(buffer.readString(114));
        this.tab = buffer.readString(114);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(this.uuid.toString());
        buffer.writeString(this.tab);
    }

    public static class PacketGetItemHandler implements IMessageHandler<PacketGetItem,IMessage>{

        @Override
        public IMessage onMessage(PacketGetItem message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            if(!inventoryFolder.exists()){
                return null;
            }
            File inventoryFile = new File(inventoryFolder,message.tab+".json");

            if(inventoryFile.exists()){
                try{
                    String filePath =inventoryFile.getAbsolutePath();
                    FileReader reader = new FileReader(filePath);
                    JsonParser jsonParser = new JsonParser();
                    JsonElement jsonElement = jsonParser.parse(reader);
                    Gson gson = new Gson();
                    Map<String, Map<String,Integer>> data = gson.fromJson(jsonElement, new TypeToken<Map<String, Map<String, Integer>>>(){}.getType());

                    CustomInventory.temp = data.get(message.uuid.toString());
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
