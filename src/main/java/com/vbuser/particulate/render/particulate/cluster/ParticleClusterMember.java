package com.vbuser.particulate.render.particulate.cluster;

import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;

public class ParticleClusterMember extends Particle {

    private final double lx, ly, lz;
    private final ParticleCluster cluster;

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

    @Override
    public void onUpdate() {
        if (particleAge++ >= particleMaxAge) { setExpired(); return; }
        prevPosX = posX;
        prevPosY = posY;
        prevPosZ = posZ;
        double x = lx * cluster.scale;
        double y = ly * cluster.scale;
        double z = lz * cluster.scale;
        double cy = Math.cos(cluster.yaw), sy = Math.sin(cluster.yaw);
        double cp = Math.cos(cluster.pitch), sp = Math.sin(cluster.pitch);
        double cr = Math.cos(cluster.roll), sr = Math.sin(cluster.roll);
        double rx = x * cy + z * sy;
        double rz = -x * sy + z * cy;
        double rry = y * cr - rz * sr;
        double rrz = y * sr + rz * cr;
        double fz = rrz * cp - rry * sp;
        double fy = rrz * sp + rry * cp;
        posX = cluster.ox + rx;
        posY = cluster.oy + fy;
        posZ = cluster.oz + fz;
    }

    @Override
    public void renderParticle(BufferBuilder b, Entity cam, float pt,
                               float rx, float rz, float ryz, float rxy, float rxz) {
        double cx = cam.lastTickPosX + (cam.posX - cam.lastTickPosX) * pt;
        double cy = cam.lastTickPosY + (cam.posY - cam.lastTickPosY) * pt;
        double cz = cam.lastTickPosZ + (cam.posZ - cam.lastTickPosZ) * pt;
        float x = (float) (posX - cx);
        float y = (float) (posY - cy);
        float z = (float) (posZ - cz);
        float s = particleScale;
        float u0 = 0f, u1 = 1f / 16f;
        float v0 = 0f, v1 = 1f / 16f;
        int br = 0xF000F0;
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