//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:ChatGPT(LMArena)
//AI注释服务提供商:DeepSeek

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

/**
 * PacketTSB 类用于向服务器请求同步方块状态<br>
 * 实现了IMessage接口，用于网络序列化和反序列化<br>
 * TSB代表"To Server Block"
 */
public class PacketTSB implements IMessage {

    public BlockPos pos;  // 方块位置

    /**
     * 无参构造函数，Forge网络框架需要
     */
    public PacketTSB() {
    }

    /**
     * 带参构造函数
     * @param pos 方块的位置
     */
    public PacketTSB(BlockPos pos) {
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
    }

    /**
     * PacketTSB的消息处理器类<br>
     * 负责在服务器端接收请求并广播当前方块状态给所有客户端
     */
    public static class Handler implements IMessageHandler<PacketTSB, IMessage> {

        /**
         * 处理接收到的PacketTSB消息
         * @param message 接收到的数据包
         * @param ctx 消息上下文
         * @return 返回响应消息，此处为null表示无响应
         */
        @Override
        public IMessage onMessage(PacketTSB message, MessageContext ctx) {
            // 只在服务器端执行方块状态同步
            if (ctx.side == Side.SERVER) {
                World world = ctx.getServerHandler().player.world;
                IBlockState state = world.getBlockState(message.pos);
                // 向所有客户端发送当前方块状态
                Particulate.networkWrapper.sendToAll(new PacketTCB(state, message.pos));
            }
            return null;
        }
    }
}