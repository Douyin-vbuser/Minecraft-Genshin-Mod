//本文件包含AI辅助生成内容
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.command;

import com.vbuser.particulate.Particulate;
import com.vbuser.particulate.network.particle.PacketCommon;
import com.vbuser.particulate.network.particle.PacketSimple;
import com.vbuser.particulate.render.particulate.cluster.ClusterSpawner;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

/**
 * CmdP 类实现了一个自定义的 Minecraft 命令 "/cmdp"，用于生成粒子效果。<br>
 * 支持三种模式：<br>
 * 1. 普通粒子（通过ID指定）<br>
 * 2. 表达式粒子（ID 50）<br>
 * 3. 簇粒子（ID 51，使用数学表达式生成粒子簇）
 *
 * 示例用法：<br>
 * - 普通粒子: cmdp 1 10 20 30<br>
 * - 表达式粒子: cmdp 50 10 20 30<br>
 * - 簇粒子: cmdp 51 10 20 30
 */
@SuppressWarnings("all")
public class CmdP extends CommandBase {

    /**
     * 获取命令名称
     */
    @Override
    public String getName() {
        return "cmdp";
    }

    /**
     * 获取命令使用说明
     */
    @Override
    public String getUsage(ICommandSender sender) {
        return "cmdp <id> <x> <y> <z>";
    }

    /**
     * 执行命令的主要逻辑<br>
     * 根据粒子ID生成对应类型的粒子效果
     */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        // 验证参数数量
        if (args.length < 4) {
            throw new CommandException("Invalid number of arguments. Usage: " + getUsage(sender));
        }

        try {
            // 解析粒子ID和坐标参数
            int particleId = Integer.parseInt(args[0]);
            double x = parseDouble(args[1]);
            double y = parseDouble(args[2]);
            double z = parseDouble(args[3]);

            // 处理特殊粒子ID 50
            if (particleId == 50) {
                Particulate.networkWrapper.sendToAll(new PacketSimple(x, y, z));
                return;
            }

            // 处理特殊粒子ID 51（簇粒子）
            if (particleId == 51) {
                ClusterSpawner.spawn(sender.getEntityWorld(), x, y, z,
                        "x*x+y*y+z*z-1", 0.2,
                        120, 255, 80, 255);
                return;
            }

            // 处理普通粒子
            EnumParticleTypes particle = EnumParticleTypes.values()[particleId];
            World world = sender.getEntityWorld();
            // 在服务端生成粒子
            world.spawnParticle(particle, x, y, z, 0.0, 0.0, 0.0);
            // 向所有客户端发送粒子生成数据包
            Particulate.networkWrapper.sendToAll(new PacketCommon(x, y, z, particleId));
        } catch (NumberFormatException e) {
            throw new CommandException("Invalid number format. Usage: " + getUsage(sender));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommandException("Invalid particle ID. Usage: " + getUsage(sender));
        }
    }
}