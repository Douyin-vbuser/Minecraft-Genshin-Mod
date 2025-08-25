package com.vbuser.particulate.render.particulate.cluster;

import net.minecraft.client.Minecraft;

import java.util.ArrayList;
import java.util.List;

public class ParticleCluster {

    public final double ox, oy, oz;
    public double scale = 1;
    public double yaw = 0, pitch = 0, roll = 0;
    private final List<ParticleClusterMember> list = new ArrayList<>();

    public ParticleCluster(double ox, double oy, double oz) {
        this.ox = ox;
        this.oy = oy;
        this.oz = oz;
    }

    public void add(ParticleClusterMember p) { list.add(p); }

    public void spawn() { list.forEach(Minecraft.getMinecraft().effectRenderer::addEffect); }
}
