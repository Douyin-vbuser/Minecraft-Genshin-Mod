package com.vbuser.particulate.network.block;

import com.vbuser.particulate.Particulate;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketTSB implements IMessage {

    public BlockPos pos;

    public PacketTSB() {
    }

    public PacketTSB(BlockPos pos) {
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.pos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
    }

    public static class Handler implements IMessageHandler<PacketTSB, IMessage> {
        @Override
        public IMessage onMessage(PacketTSB message, MessageContext ctx) {
            if (ctx.side == Side.SERVER) {
                World world = ctx.getServerHandler().player.world;
                IBlockState state = world.getBlockState(message.pos);
                Particulate.networkWrapper.sendToAll(new PacketTCB(state, message.pos));
            }
            return null;
        }
    }
}
