package com.vbuser.particulate.network.block;

import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

public class PacketTCB implements IMessage {

    public IBlockState state;
    public BlockPos pos;

    public PacketTCB() {
    }

    public PacketTCB(IBlockState state, BlockPos pos) {
        this.state = state;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.pos = new BlockPos(x, y, z);

        Block block = Block.getBlockById(buf.readInt());
        this.state = block.getStateFromMeta(buf.readInt());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(Block.getIdFromBlock(this.state.getBlock()));
        buf.writeInt(state.getBlock().getMetaFromState(state));
    }

    public static class Handler implements IMessageHandler<PacketTCB, IMessage> {
        @Override
        public IMessage onMessage(PacketTCB message, MessageContext ctx) {
            if (ctx.side == Side.CLIENT) {
                World world = Minecraft.getMinecraft().world;
                world.setBlockState(message.pos, message.state);
            }
            return null;
        }
    }
}
