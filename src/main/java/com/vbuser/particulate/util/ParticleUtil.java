package com.vbuser.particulate.util;

import com.vbuser.particulate.render.particulate.ParticulateLeave;
import com.vbuser.particulate.render.particulate.ParticulateSimple;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.util.EnumParticleTypes;
import net.minecraftforge.common.util.EnumHelper;

/**
 * ParticleUtil 类提供粒子系统注册功能<br>
 * 用于动态注册自定义粒子类型到Minecraft的粒子系统中
 */
public class ParticleUtil {

    /**
     * 注册粒子系统到Minecraft的粒子类型枚举中
     * @param enumName 枚举名称
     * @param name 粒子名称
     * @param id 粒子ID（必须唯一）
     * @param ignoreRange 是否忽略范围限制（true表示在任何距离都可见）
     * @param argumentCount 参数数量
     * @param factory 粒子工厂
     * @return 注册的粒子类型枚举
     * @throws RuntimeException 如果ID已存在则抛出异常
     */
    public static EnumParticleTypes registerParticleSystem(String enumName, String name, int id, boolean ignoreRange, int argumentCount, IParticleFactory factory) {
        // 检查ID是否已存在
        for (EnumParticleTypes existingParticle : EnumParticleTypes.values()) {
            if (existingParticle.getParticleID() == id) {
                throw new RuntimeException("Something attempted to register a particle with the same integer ID as " + existingParticle.getParticleName() + " (" + existingParticle + ").");
            }
        }

        // 使用EnumHelper动态添加枚举值
        EnumParticleTypes particle = EnumHelper.addEnum(EnumParticleTypes.class, enumName,
                new Class[]{String.class, int.class, boolean.class, int.class},
                name, id, ignoreRange, argumentCount);

        // 注册粒子工厂
        Minecraft.getMinecraft().effectRenderer.registerParticle(id, factory);

        return particle;
    }

    // 自定义粒子类型
    public static EnumParticleTypes LEAVE;   // 树叶粒子
    public static EnumParticleTypes SIMPLE;  // 简单表达式粒子

    /**
     * 初始化方法，注册所有自定义粒子类型
     */
    public static void init() {
        LEAVE = registerParticleSystem("PARTICULATE_LEAVE", "leave", 49, true, 0, new ParticulateLeave.Factory());
        SIMPLE = registerParticleSystem("PARTICULATE_SIMPLE","simple",50,true,0,new ParticulateSimple.Factory());
    }
}