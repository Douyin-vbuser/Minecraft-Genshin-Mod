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

public class PacketArtifactNBT implements IMessage {

    String player_id, artifact_id, key;
    int value;

    public PacketArtifactNBT() {
    }

    public PacketArtifactNBT(String player_id, String artifact_id, String key, int value) {
        this.player_id = player_id;
        this.artifact_id = artifact_id;
        this.key = key;
        this.value = value;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.player_id = buffer.readString(114);
        this.artifact_id = buffer.readString(114);
        this.key = buffer.readString(114);
        this.value = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(this.player_id);
        buffer.writeString(this.artifact_id);
        buffer.writeString(this.key);
        buffer.writeInt(this.value);
    }

    public static class PacketArtifactNBTHandler implements IMessageHandler<PacketArtifactNBT,IMessage> {

        @Override
        public IMessage onMessage(PacketArtifactNBT message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            File inventoryFile = new File(inventoryFolder,"sheng_yi_wu.json");

            try{
                Map<String,Map<String, Map<String,Integer>>> existingData = new HashMap<>();
                if(inventoryFile.exists()){
                    String existingJson = FileUtils.readFileToString(inventoryFile, StandardCharsets.UTF_8);
                    existingData = new Gson().fromJson(existingJson,new TypeToken<Map<String,Map<String, Map<String,Integer>>>>(){}.getType());
                }

                existingData.get(message.player_id).get(message.artifact_id).put(message.key, message.value);

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
