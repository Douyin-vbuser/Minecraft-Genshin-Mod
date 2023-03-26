package com.vbuser.genshin.network;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import static com.vbuser.genshin.gui.GuiCookMain.*;

public class PacketInventaireClient implements IMessage {

    private int PacketMoGuCount,PacketQingRouCount,PacketNiaoDanCount,PacketShouRouCount,PacketLuoBoCount,
            PacketBoHeCount, PacketJueYunJiaoJiaoCount,PacketTianTianHuaCount,PacketJuanXingCaiCount,
            PacketPingGuoCount,PacketTuDouCount, PacketNiuNaiCount,PacketTangCount,PacketXingRengCount,
            PacketSongRongCount;

    public PacketInventaireClient(){

    }

    public PacketInventaireClient(int MoGuCount,int QingRouCount,int NiaoDanCount, int ShouRouCount,int LuoBoCount,int BoHeCount,int JueYunJiaoJiaoCount,int TianTianHuaCount, int JuanXingCaiCount,int PingGuoCount,int TuDouCount,int TangCount,int NiuNaiCount,int XingRengCount,int SongRongCount) {
        this.PacketMoGuCount=MoGuCount;
        this.PacketQingRouCount=QingRouCount;
        this.PacketNiaoDanCount=NiaoDanCount;
        this.PacketShouRouCount=ShouRouCount;
        this.PacketLuoBoCount=LuoBoCount;
        this.PacketBoHeCount=BoHeCount;
        this.PacketJueYunJiaoJiaoCount=JueYunJiaoJiaoCount;
        this.PacketTianTianHuaCount=TianTianHuaCount;
        this.PacketJuanXingCaiCount=JuanXingCaiCount;
        this.PacketPingGuoCount=PingGuoCount;
        this.PacketTuDouCount= TuDouCount;
        this.PacketNiuNaiCount=NiuNaiCount;
        this.PacketTangCount=TangCount;
        this.PacketXingRengCount=XingRengCount;
        this.PacketSongRongCount=SongRongCount;
    }


    @Override
    public void fromBytes(ByteBuf buf) {
        this.PacketMoGuCount=buf.readInt();
        this.PacketQingRouCount=buf.readInt();
        this.PacketNiaoDanCount=buf.readInt();
        this.PacketShouRouCount=buf.readInt();
        this.PacketLuoBoCount=buf.readInt();
        this.PacketBoHeCount=buf.readInt();
        this.PacketJueYunJiaoJiaoCount=buf.readInt();
        this.PacketTianTianHuaCount=buf.readInt();
        this.PacketJuanXingCaiCount=buf.readInt();
        this.PacketPingGuoCount=buf.readInt();
        this.PacketTuDouCount=buf.readInt();
        this.PacketNiuNaiCount=buf.readInt();
        this.PacketTangCount=buf.readInt();
        this.PacketXingRengCount=buf.readInt();
        this.PacketSongRongCount=buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(PacketMoGuCount);
        buf.writeInt(PacketQingRouCount);
        buf.writeInt(PacketNiaoDanCount);
        buf.writeInt(PacketShouRouCount);
        buf.writeInt(PacketLuoBoCount);
        buf.writeInt(PacketBoHeCount);
        buf.writeInt(PacketJueYunJiaoJiaoCount);
        buf.writeInt(PacketTianTianHuaCount);
        buf.writeInt(PacketJuanXingCaiCount);
        buf.writeInt(PacketPingGuoCount);
        buf.writeInt(PacketTuDouCount);
        buf.writeInt(PacketNiuNaiCount);
        buf.writeInt(PacketTangCount);
        buf.writeInt(PacketXingRengCount);
        buf.writeInt(PacketSongRongCount);
    }

    public static class Handler implements IMessageHandler<PacketInventaireClient, IMessage> {


        @Override
        public IMessage onMessage(PacketInventaireClient message, MessageContext ctx) {
            MoGuCount=message.PacketMoGuCount;
            QingRouCount=message.PacketQingRouCount;
            NiaoDanCount=message.PacketNiaoDanCount;
            ShouRouCount=message.PacketShouRouCount;
            LuoBoCount=message.PacketLuoBoCount;
            BoHeCount=message.PacketBoHeCount;
            JueYunJiaoJiaoCount=message.PacketJueYunJiaoJiaoCount;
            TianTianHuaCount=message.PacketTianTianHuaCount;
            JuanXingCaiCount=message.PacketJuanXingCaiCount;
            PingGuoCount=message.PacketPingGuoCount;
            TuDouCount=message.PacketTuDouCount;
            NiuNaiCount=message.PacketNiuNaiCount;
            TangCount=message.PacketTangCount;
            XingRengCount=message.PacketXingRengCount;
            SongRongCount=message.PacketSongRongCount;
            return null;
        }
    }
}
