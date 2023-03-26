package com.vbuser.genshin.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketInventaireServer implements IMessage {

    public PacketInventaireServer() {
    }

    public PacketInventaireServer(int index, int isGood) {

    }


    @Override
    public void fromBytes(ByteBuf buf) {

    }

    @Override
    public void toBytes(ByteBuf buf) {
    }

    public static class Handler implements IMessageHandler<PacketInventaireServer, PacketInventaireClient> {


        @Override
        public PacketInventaireClient onMessage(PacketInventaireServer message, MessageContext ctx) {
            int MoGuCount=0,QingRouCount=0,NiaoDanCount=0,ShouRouCount=0,LuoBoCount=0,BoHeCount=0,
            JueYunJiaoJiaoCount=0,TianTianHuaCount=0,JuanXingCaiCount=0,PingGuoCount=0,TuDouCount=0,
            NiuNaiCount=0,TangCount=0,XingRengCount=0,SongRongCount=0;
            for(int item = 0; item<ctx.getServerHandler().player.inventory.mainInventory.size();item++){
                String name=String.valueOf(ctx.getServerHandler().player.inventory.mainInventory.get(item).getItem().getRegistryName());
                int count = ctx.getServerHandler().player.inventory.mainInventory.get(item).getCount();
                switch (name){
                    case "genshin:mo_gu":MoGuCount+=count;break;
                    case "genshin:qing_rou":QingRouCount+=count;break;
                    case "genshin:niao_dan":NiaoDanCount+=count;break;
                    case "genshin:shou_rou":ShouRouCount+=count;break;
                    case "genshin:luo_bo":LuoBoCount+=count;break;
                    case "genshin:bo_he":BoHeCount+=count;break;
                    case "genshin:jue_yun_jiao_jiao":JueYunJiaoJiaoCount+=count;break;
                    case "genshin:tian_tian_hua":TianTianHuaCount+=count;break;
                    case "genshin:juan_xing_cai":JuanXingCaiCount+=count;break;
                    case "genshin:ping_guo":PingGuoCount+=count;break;
                    case "genshin:tu_dou":TuDouCount+=count;break;
                    case "genshin:niu_nai":NiuNaiCount+=count;break;
                    case "genshin:tang":TangCount+=count;break;
                    case "genshin:xing_reng":XingRengCount+=count;break;
                    case "genshin:song_rong":SongRongCount+=count;break;
                }
            }
            return new PacketInventaireClient(MoGuCount,QingRouCount,NiaoDanCount, ShouRouCount,LuoBoCount,
            BoHeCount,JueYunJiaoJiaoCount,TianTianHuaCount, JuanXingCaiCount,PingGuoCount,TuDouCount,TangCount,NiuNaiCount,
            XingRengCount,SongRongCount);
        }
    }
}