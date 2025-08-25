package com.vbuser.particulate.render.particulate.cluster;

import com.vbuser.particulate.math.ExprNode;
import com.vbuser.particulate.math.ExpressionParser;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

public class ClusterSpawner {
    public static ParticleCluster spawn(World w, double x, double y, double z,
                                        String solidExpr, double step,
                                        int life, float r, float g, float b) {
        ParticleCluster c = new ParticleCluster(x, y, z);
        ExpressionParser p = new ExpressionParser();
        ExprNode ast = p.compile(solidExpr);
        Map<String, Double> variables = new HashMap<>();

        for (double i = -1; i <= 1; i += step)
            for (double j = -1; j <= 1; j += step)
                for (double k = -1; k <= 1; k += step) {
                    variables.put("x", i);
                    variables.put("y", j);
                    variables.put("z", k);
                    if (ast.evaluate(variables) <= 0) {
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
