//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:ChatGPT(LMArena)
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.render.particulate.cluster;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

/**
 * ParticleClusterMember 类表示粒子簇中的单个粒子成员<br>
 * 继承自Minecraft的Particle类，支持复杂的变换和渲染
 */
public class ParticleClusterMember extends Particle {

    private final double lx, ly, lz;  // 相对于簇中心的局部坐标
    private final ParticleCluster cluster;  // 所属的粒子簇

    /**
     * 构造函数
     * @param w 世界对象
     * @param ox 簇中心点的x坐标
     * @param oy 簇中心点的y坐标
     * @param oz 簇中心点的z坐标
     * @param lx 相对于簇中心的x坐标
     * @param ly 相对于簇中心的y坐标
     * @param lz 相对于簇中心的z坐标
     * @param r 红色分量 (0-1)
     * @param g 绿色分量 (0-1)
     * @param b 蓝色分量 (0-1)
     * @param a 透明度 (0-1)
     * @param s 粒子尺寸
     * @param life 粒子生命周期
     * @param c 所属的粒子簇
     */
    public ParticleClusterMember(World w, double ox, double oy, double oz,
                                 double lx, double ly, double lz,
                                 float r, float g, float b, float a,
                                 float s, int life, ParticleCluster c) {
        super(w, ox, oy, oz);
        this.lx = lx;
        this.ly = ly;
        this.lz = lz;
        this.cluster = c;
        particleRed = r;
        particleGreen = g;
        particleBlue = b;
        particleAlpha = a;
        particleScale = s;
        particleMaxAge = life;
        canCollide = false;
        setParticleTextureIndex(0);
    }

    /**
     * 更新粒子状态<br>
     * 根据粒子簇的变换参数计算粒子的世界坐标
     */
    @Override
    public void onUpdate() {
        // 检查粒子生命周期
        if (particleAge++ >= particleMaxAge) { setExpired(); return; }
        // 保存上一帧的位置
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;

        // 应用缩放
        double x = lx * cluster.scale;
        double y = ly * cluster.scale;
        double z = lz * cluster.scale;

        // 计算旋转矩阵分量
        double cy = Math.cos(cluster.yaw), sy = Math.sin(cluster.yaw);
        double cp = Math.cos(cluster.pitch), sp = Math.sin(cluster.pitch);
        double cr = Math.cos(cluster.roll), sr = Math.sin(cluster.roll);

        // 应用偏航旋转 (绕Y轴)
        double rx = x * cy + z * sy;
        double rz = -x * sy + z * cy;

        // 应用滚动旋转 (绕X轴)
        double rry = y * cr - rz * sr;
        double rrz = y * sr + rz * cr;

        // 应用俯仰旋转 (绕Z轴)
        double fz = rrz * cp - rry * sp;
        double fy = rrz * sp + rry * cp;

        // 计算最终的世界坐标
        posX = cluster.ox + rx;
        posY = cluster.oy + fy;
        posZ = cluster.oz + fz;
    }

    /**
     * 渲染粒子<br>
     * 使用BufferBuilder构建粒子的几何图形
     * @param b 缓冲区构建器
     * @param cam 摄像机实体
     * @param pt 部分帧时间，用于平滑插值
     * @param rx 旋转矩阵的X分量
     * @param rz 旋转矩阵的Z分量
     * @param ryz 旋转矩阵的YZ分量
     * @param rxy 旋转矩阵的XY分量
     * @param rxz 旋转矩阵的XZ分量
     */
    @Override
    public void renderParticle(BufferBuilder b, Entity cam, float pt,
                               float rx, float rz, float ryz, float rxy, float rxz) {
        // 计算摄像机的插值位置
        double cx = cam.lastTickPosX + (cam.posX - cam.lastTickPosX) * pt;
        double cy = cam.lastTickPosY + (cam.posY - cam.lastTickPosY) * pt;
        double cz = cam.lastTickPosZ + (cam.posZ - cam.lastTickPosZ) * pt;

        // 计算粒子相对于摄像机的位置
        float x = (float) (posX - cx);
        float y = (float) (posY - cy);
        float z = (float) (posZ - cz);

        // 设置粒子尺寸
        float s = particleScale;

        // 设置纹理坐标 (使用固定的纹理子区域)
        float u0 = 0f, u1 = 1f / 16f;
        float v0 = 0f, v1 = 1f / 16f;

        // 设置光照值 (固定值)
        int br = 0xF000F0;

        // 构建粒子的四个顶点 (四边形)
        b.pos(x - rx * s - rxy * s, y - rz * s, z - ryz * s - rxz * s).tex(u0, v0)
                .color(particleRed, particleGreen, particleBlue, particleAlpha)
                .lightmap(br >> 16 & 65535, br & 65535).endVertex();
        b.pos(x - rx * s + rxy * s, y + rz * s, z - ryz * s + rxz * s).tex(u0, v1)
                .color(particleRed, particleGreen, particleBlue, particleAlpha)
                .lightmap(br >> 16 & 65535, br & 65535).endVertex();
        b.pos(x + rx * s + rxy * s, y + rz * s, z + ryz * s + rxz * s).tex(u1, v1)
                .color(particleRed, particleGreen, particleBlue, particleAlpha)
                .lightmap(br >> 16 & 65535, br & 65535).endVertex();
        b.pos(x + rx * s - rxy * s, y - rz * s, z + ryz * s - rxz * s).tex(u1, v0)
                .color(particleRed, particleGreen, particleBlue, particleAlpha)
                .lightmap(br >> 16 & 65535, br & 65535).endVertex();
    }
}