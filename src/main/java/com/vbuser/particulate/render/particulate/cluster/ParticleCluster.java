//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:ChatGPT(LMArena)
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.render.particulate.cluster;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

/**
 * ParticleCluster 类表示一个粒子簇容器<br>
 * 管理一组相关的粒子，并提供整体的变换控制
 */
public class ParticleCluster {

    public final double ox, oy, oz;  // 粒子簇中心点的原始坐标
    public double scale = 1;         // 缩放因子
    public double yaw = 0, pitch = 0, roll = 0;  // 旋转角度
    private final List<ParticleClusterMember> list = new ArrayList<>();  // 粒子成员列表

    /**
     * 构造函数
     * @param ox 中心点x坐标
     * @param oy 中心点y坐标
     * @param oz 中心点z坐标
     */
    public ParticleCluster(double ox, double oy, double oz) {
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
    }

    /**
     * 添加粒子成员到簇中
     * @param p 粒子成员
     */
    public void add(ParticleClusterMember p) { list.add(p); }

    /**
     * 渲染粒子簇中的所有粒子
     */
    public void spawn() { list.forEach(Minecraft.getMinecraft().effectRenderer::addEffect); }
}