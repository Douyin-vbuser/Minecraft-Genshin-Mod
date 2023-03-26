package com.vbuser.genshin.network;

import com.vbuser.genshin.init.ModItems;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.ArrayList;

public class PacketGiveServer implements IMessage {

    private int index;
    private int isGood;
    private int quantity;


    public PacketGiveServer() {
    }

    public PacketGiveServer(int index, int isGood, int quantity) {
        this.index = index;
        this.isGood = isGood;
        this.quantity = quantity;
    }


    @Override
    public void fromBytes(ByteBuf buf) {

        index = buf.readInt();
        isGood = buf.readInt();
        quantity = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(index);
        buf.writeInt(isGood);
        buf.writeInt(quantity);
    }

    public static class Handler implements IMessageHandler<PacketGiveServer, IMessage> {
        @Override
        public IMessage onMessage(PacketGiveServer message, MessageContext ctx){
            Item item = null;
            ArrayList clear = new ArrayList();
            for(int i = 0; i< message.quantity;i++){
                switch (message.index){
                    case 0:
                        if(message.isGood==0){item= ModItems.YE_GU_JI_ROU_CHUANG_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.YE_GU_JI_ROU_CHUANG;}
                            else{item=ModItems.YE_GU_JI_ROU_CHUANG_GOOD;}
                        }
                    case 1:
                        if(message.isGood==0){item= ModItems.TI_WA_TE_JIAN_DAN_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.TI_WA_TE_JIAN_DAN;}
                            else{item=ModItems.TI_WA_TE_JIAN_DAN_GOOD;}
                        }
                    case 2:
                        if(message.isGood==0){item= ModItems.KAO_ROU_PAI_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.KAO_ROU_PAI;}
                            else{item=ModItems.KAO_ROU_PAI_GOOD;}
                        }
                    case 3:
                        if(message.isGood==0){item= ModItems.LUO_BO_SHI_SHU_TANG_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.LUO_BO_SHI_SHU_TANG;}
                            else{item=ModItems.LUO_BO_SHI_SHU_TANG_GOOD;}
                        }
                    case 4:
                        if(message.isGood==0){item= ModItems.LUO_BO_SHI_SHU_TANG_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.LUO_BO_SHI_SHU_TANG;}
                            else{item=ModItems.LUO_BO_SHI_SHU_TANG_GOOD;}
                        }
                    case 5:
                        if(message.isGood==0){item= ModItems.BAO_CHAO_ROU_PIAN_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.BAO_CHAO_ROU_PIAN;}
                            else{item=ModItems.BAO_CHAO_ROU_PIAN_GOOD;}
                        }
                    case 6:
                        if(message.isGood==0){item= ModItems.TIAN_TIAN_HUA_NIANG_JI_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.TIAN_TIAN_HUA_NIANG_JI;}
                            else{item=ModItems.TIAN_TIAN_HUA_NIANG_JI_GOOD;}
                        }
                    case 7:
                        if(message.isGood==0){item= ModItems.MAN_ZU_SHA_LA_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.MAN_ZU_SHA_LA;}
                            else{item=ModItems.MAN_ZU_SHA_LA_GOOD;}
                        }
                    case 8:
                        if(message.isGood==0){item= ModItems.XING_RENG_DOU_FU_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.XING_RENG_DOU_FU;}
                            else{item=ModItems.XING_RENG_DOU_FU_GOOD;}
                        }
                    case 9:
                        if(message.isGood==0){item= ModItems.SONG_RONG_NIANG_ROU_JUAN_BAD;}
                        else{
                            if(message.isGood==1){item=ModItems.SONG_RONG_NIANG_ROU_JUAN;}
                            else{item=ModItems.SONG_RONG_NIANG_ROU_JUAN_GOOD;}
                        }
                    default:throw new IllegalStateException("Unexpected value: " + message.index);
                }
            }
            ctx.getServerHandler().player.inventory.addItemStackToInventory(new ItemStack(item, message.quantity));

            for(int it = 0 ; it < clear.size(); it++){

                for (int i = 0; i < 35; i++){
                    if (ctx.getServerHandler().player.inventory.getStackInSlot(i).getItem().equals(clear.get(it))){
                        ctx.getServerHandler().player.inventory.getStackInSlot(i).setCount(ctx.getServerHandler().player.inventory.getStackInSlot(i).getCount() - 1);break;
                    }
                }
            }

            return null;
        }
    }
}