package com.vbuser.particulate.render.particulate.cluster;

import com.vbuser.particulate.math.ExpressionParser;
import net.minecraft.world.World;

public class ClusterSpawner {

    public static ParticleCluster spawn(World w, double x, double y, double z,
                                        String solidExpr, double step,
                                        int life, float r, float g, float b) {
        ParticleCluster c = new ParticleCluster(x, y, z);
        ExpressionParser p = new ExpressionParser();
        for (double i = -1; i <= 1; i += step)
            for (double j = -1; j <= 1; j += step)
                for (double k = -1; k <= 1; k += step) {
                    p.setVariable("x", i);
                    p.setVariable("y", j);
                    p.setVariable("z", k);
                    if (p.parse(solidExpr) <= 0) {
                        ParticleClusterMember m = new ParticleClusterMember(
                                w, x, y, z,
                                i, j, k,
                                r, g, b, 1f,
                                0.05f, life, c);
                        c.add(m);
                    }
                }
        c.spawn();
        return c;
    }
}
