package com.vbuser.particulate.command;

import com.vbuser.particulate.Particulate;
import net.minecraft.block.Block;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

/**
 * CmdB 类实现了一个自定义的 Minecraft 命令 "/cmdb"，用于向指定玩家发送方块渲染指令。<br>
 * 支持两种模式：单方块渲染和区域填充渲染（类似原版 fill 命令）。<br>
 * <br>
 * 示例用法：<br>
 * - 单方块: cmdb @p setblock 10 20 30 minecraft:grass 0<br>
 * - 区域填充: cmdb @a fill 5 5 5 15 15 15 minecraft:grass 0
 */
@SuppressWarnings("all")
public class CmdB extends CommandBase {

    /**
     * 获取命令名称
     */
    @Override
    public String getName() {
        return "cmdb";
    }

    /**
     * 获取命令使用说明
     */
    @Override
    public String getUsage(ICommandSender sender) {
        return "cmdb <player> <command> <x> <y> <z> <name> <meta>";
    }

    /**
     * 执行命令的主要逻辑<br>
     * 根据参数执行单区块渲染或区域填充渲染
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // 获取目标玩家实体
        EntityPlayerMP player = getPlayer(server, sender, args[0]);

        if (args[1].equals("fill")) {
            // 解析区域填充的起始和结束坐标
            int x_from = Integer.parseInt(args[2]);
            int y_from = Integer.parseInt(args[3]);
            int z_from = Integer.parseInt(args[4]);
            int x_to = Integer.parseInt(args[5]);
            int y_to = Integer.parseInt(args[6]);
            int z_to = Integer.parseInt(args[7]);

            // 获取方块对象和元数据
            Block block = Block.getBlockFromName(args[8]);
            int meta = Integer.parseInt(args[9]);

            // 遍历区域内的每个坐标点
            for (int i = Math.min(x_from, x_to); i <= Math.max(x_from, x_to); i++) {
                for (int j = Math.min(y_from, y_to); j <= Math.max(y_from, y_to); j++) {
                    for (int k = Math.min(z_from, z_to); k <= Math.max(z_from, z_to); k++) {
                        BlockPos pos = new BlockPos(i, j, k);
                        // 向玩家发送区块渲染数据包
                        Particulate.renderBlock(player, pos, block, meta);
                    }
                }
            }
        } else {
            // 解析单区块坐标
            int x = Integer.parseInt(args[2]);
            int y = Integer.parseInt(args[3]);
            int z = Integer.parseInt(args[4]);
            BlockPos pos = new BlockPos(x, y, z);

            // 获取方块对象和元数据
            Block block = Block.getBlockFromName(args[5]);
            int meta = Integer.parseInt(args[6]);

            // 向玩家发送单区块渲染数据包
            Particulate.renderBlock(player, pos, block, meta);
        }
    }
}