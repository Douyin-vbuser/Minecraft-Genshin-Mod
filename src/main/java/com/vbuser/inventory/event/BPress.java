package com.vbuser.inventory.event;

import com.vbuser.inventory.CustomInventory;
import com.vbuser.inventory.proxy.ClientProxy;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.UUID;

public class BPress {
    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event){
        if (Minecraft.getMinecraft().player != null && ClientProxy.B.isPressed()){
            BlockPos pos = new BlockPos(Minecraft.getMinecraft().player);
            CustomInventory.network.sendToServer(new PacketInventoryGui(pos.getX(),pos.getY(),pos.getZ(),Minecraft.getMinecraft().player.getUniqueID().toString()));
            Minecraft.getMinecraft().player.openGui(CustomInventory.instance,1,Minecraft.getMinecraft().world, pos.getX(), pos.getY(), pos.getZ());
        }
    }

    public static class PacketInventoryGui implements IMessage{

        int x,y,z;
        String uuid;

        public PacketInventoryGui(){}

        public PacketInventoryGui(int x,int y,int z,String uuid){
            this.x = x;
            this.y = y;
            this.z = z;
            this.uuid = uuid;
        }

        @Override
        public void fromBytes(ByteBuf buf) {
            PacketBuffer buffer = new PacketBuffer(buf);
            this.x = buffer.readInt();
            this.y = buffer.readInt();
            this.z = buffer.readInt();
            this.uuid = buffer.readString(114);
        }

        @Override
        public void toBytes(ByteBuf buf) {
            PacketBuffer buffer = new PacketBuffer(buf);
            buffer.writeInt(x);
            buffer.writeInt(y);
            buffer.writeInt(z);
            buffer.writeString(uuid);
        }
    }

    public static class PacketInventoryGuiHandler implements IMessageHandler<PacketInventoryGui, IMessage> {
        @Override
        public IMessage onMessage(PacketInventoryGui message, MessageContext ctx) {
            EntityPlayer player = Minecraft.getMinecraft().world.getPlayerEntityByUUID(UUID.fromString(message.uuid));
            if (player != null) {
                player.openGui(CustomInventory.instance, 1, Minecraft.getMinecraft().world, message.x, message.y, message.z);
            }
            return null;
        }
    }
}
