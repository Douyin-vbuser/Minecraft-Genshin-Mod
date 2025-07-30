package com.vbuser.particulate.network.block;

import com.vbuser.particulate.Particulate;
import com.vbuser.particulate.render.BlockRenderer;
import io.netty.buffer.ByteBuf;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;

@SuppressWarnings("all")
public class PacketBlock implements IMessage {

    public Block block;
    public int meta;
    public BlockPos pos;

    public PacketBlock() {
    }

    public PacketBlock(Block block, int meta, BlockPos pos) {
        this.block = block;
        this.meta = meta;
        this.pos = pos;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.block = Block.getBlockById(buf.readInt());
        this.meta = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.pos = new BlockPos(x, y, z);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(Block.getIdFromBlock(this.block));
        buf.writeInt(this.meta);
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
    }

    public static class Handler implements IMessageHandler<PacketBlock, IMessage> {
        @Override
        public IMessage onMessage(PacketBlock message, MessageContext ctx) {
            if (message.block.equals(Blocks.AIR)) {
                BlockRenderer.removeBlock(message.pos);
                if (ctx.side == Side.CLIENT) {
                    Particulate.networkWrapper.sendToServer(new PacketTSB(message.pos));
                }
            } else {
                IBlockState state = message.block.getStateFromMeta(message.meta);
                BlockRenderer.addBlock(message.pos, state);
                if (ctx.side == Side.CLIENT) {
                    Minecraft.getMinecraft().addScheduledTask(() -> {
                        World world = Minecraft.getMinecraft().world;
                        if (world != null) world.setBlockToAir(message.pos);
                    });
                }
            }
            return null;
        }
    }
}
