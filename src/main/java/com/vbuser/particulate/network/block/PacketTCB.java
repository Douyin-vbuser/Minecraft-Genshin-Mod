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

/**
 * PacketTCB 类用于在客户端设置真实世界的方块状态<br>
 * 实现了IMessage接口，用于网络序列化和反序列化<br>
 * TCB代表"To Client Block"
 */
public class PacketTCB implements IMessage {

    public IBlockState state;  // 方块状态
    public BlockPos pos;       // 方块位置

    /**
     * 无参构造函数，Forge网络框架需要
     */
    public PacketTCB() {
    }

    /**
     * 带参构造函数
     * @param state 要设置的方块状态
     * @param pos 方块的位置
     */
    public PacketTCB(IBlockState state, BlockPos pos) {
        this.state = state;
        this.pos = pos;
    }

    /**
     * 从字节缓冲区读取数据，反序列化数据包<br>
     * 读取顺序必须与toBytes方法的写入顺序一致
     * @param buf 字节缓冲区
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.pos = new BlockPos(x, y, z);

        Block block = Block.getBlockById(buf.readInt());
        this.state = block.getStateFromMeta(buf.readInt());
    }

    /**
     * 将数据写入字节缓冲区，序列化数据包<br>
     * 写入顺序必须与fromBytes方法的读取顺序一致
     * @param buf 字节缓冲区
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
        buf.writeInt(Block.getIdFromBlock(this.state.getBlock()));
        buf.writeInt(state.getBlock().getMetaFromState(state));
    }

    /**
     * PacketTCB的消息处理器类<br>
     * 负责在接收到数据包时在客户端设置方块状态
     */
    public static class Handler implements IMessageHandler<PacketTCB, IMessage> {

        /**
         * 处理接收到的PacketTCB消息
         * @param message 接收到的数据包
         * @param ctx 消息上下文
         * @return 返回响应消息，此处为null表示无响应
         */
        @Override
        public IMessage onMessage(PacketTCB message, MessageContext ctx) {
            // 只在客户端执行方块状态设置
            if (ctx.side == Side.CLIENT) {
                World world = Minecraft.getMinecraft().world;
                world.setBlockState(message.pos, message.state);
            }
            return null;
        }
    }
}