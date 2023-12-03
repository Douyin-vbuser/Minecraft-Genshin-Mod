package com.vbuser.genshin.inventory.packet;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.netty.buffer.ByteBuf;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class PacketDecreaseItem implements IMessage {

    String uuid,item;
    int count;

    public PacketDecreaseItem(){}

    public PacketDecreaseItem(String uuid,String item,int count){
        this.count = count;
        this.uuid = uuid;
        this.item = item;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        this.uuid = buffer.readString(114);
        this.item = buffer.readString(114);
        this.count = buffer.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        PacketBuffer buffer = new PacketBuffer(buf);
        buffer.writeString(uuid);
        buffer.writeString(item);
        buffer.writeInt(count);
    }

    public static class PacketDecreaseItemHandler implements IMessageHandler<PacketDecreaseItem,IMessage>{

        @Override
        public IMessage onMessage(PacketDecreaseItem message, MessageContext ctx) {
            File inventoryFolder = new File(ctx.getServerHandler().player.getServerWorld().getSaveHandler().getWorldDirectory(),"genshin_inventory");
            Item item1 = Item.getByNameOrId(message.item);
            if(item1==null){
                return null;
            }
            CreativeTabs tab = item1.getCreativeTab();
            if(tab==null){
                return null;
            }
            File inventoryFile = new File(inventoryFolder, tab.getTabLabel()+".json");

            try{

                Map<String, Map<String,Integer>> existingData;

                String existingJson = FileUtils.readFileToString(inventoryFile, StandardCharsets.UTF_8);
                existingData = new Gson().fromJson(existingJson, new TypeToken<Map<String, Map<String,Integer>>>(){}.getType());

                Map<String,Integer> playerData;
                playerData = existingData.get(message.uuid);
                playerData.put(item1.getUnlocalizedName(), playerData.get(item1.getUnlocalizedName()) - message.count);
                existingData.put(message.uuid,playerData);

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
