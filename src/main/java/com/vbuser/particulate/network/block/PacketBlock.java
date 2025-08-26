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

/**
 * PacketBlock 类用于在客户端和服务器之间传输方块渲染信息<br>
 * 实现了IMessage接口，用于网络序列化和反序列化<br>
 * 支持添加和移除自定义渲染的方块
 */
@SuppressWarnings("all")
public class PacketBlock implements IMessage {

    public Block block;    // 方块对象
    public int meta;       // 方块的元数据
    public BlockPos pos;   // 方块位置

    /**
     * 无参构造函数，Forge网络框架需要
     */
    public PacketBlock() {
    }

    /**
     * 带参构造函数
     * @param block 要渲染的方块
     * @param meta 方块的元数据
     * @param pos 方块的位置
     */
    public PacketBlock(Block block, int meta, BlockPos pos) {
        this.block = block;
        this.meta = meta;
        this.pos = pos;
    }

    /**
     * 从字节缓冲区读取数据，反序列化数据包<br>
     * 读取顺序必须与toBytes方法的写入顺序一致
     * @param buf 字节缓冲区
     */
    @Override
    public void fromBytes(ByteBuf buf) {
        this.block = Block.getBlockById(buf.readInt());
        this.meta = buf.readInt();
        int x = buf.readInt();
        int y = buf.readInt();
        int z = buf.readInt();
        this.pos = new BlockPos(x, y, z);
    }

    /**
     * 将数据写入字节缓冲区，序列化数据包<br>
     * 写入顺序必须与fromBytes方法的读取顺序一致
     * @param buf 字节缓冲区
     */
    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(Block.getIdFromBlock(this.block));
        buf.writeInt(this.meta);
        buf.writeInt(this.pos.getX());
        buf.writeInt(this.pos.getY());
        buf.writeInt(this.pos.getZ());
    }

    /**
     * PacketBlock的消息处理器类<br>
     * 负责在接收到数据包时执行相应的操作
     */
    public static class Handler implements IMessageHandler<PacketBlock, IMessage> {

        /**
         * 处理接收到的PacketBlock消息
         * @param message 接收到的数据包
         * @param ctx 消息上下文
         * @return 返回响应消息，此处为null表示无响应
         */
        @Override
        public IMessage onMessage(PacketBlock message, MessageContext ctx) {
            // 如果是空气方块，则移除渲染
            if (message.block.equals(Blocks.AIR)) {
                BlockRenderer.removeBlock(message.pos);
                // 如果是客户端，向服务器发送同步请求
                if (ctx.side == Side.CLIENT) {
                    Particulate.networkWrapper.sendToServer(new PacketTSB(message.pos));
                }
            } else {
                // 获取方块状态并添加到渲染器
                IBlockState state = message.block.getStateFromMeta(message.meta);
                BlockRenderer.addBlock(message.pos, state);
                // 如果是客户端，安排任务将实际世界的方块设置为空气
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