package com.vbuser.particulate.render.particulate.cluster;

import com.vbuser.particulate.math.ExprNode;
import com.vbuser.particulate.math.ExpressionParser;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;

/**
 * ClusterSpawner 类负责生成粒子簇<br>
 * 根据数学表达式在三维空间中生成粒子群，形成特定形状的粒子簇
 */
public class ClusterSpawner {

    /**
     * 生成并渲染一个粒子簇
     * @param w 世界对象
     * @param x 粒子簇中心点的x坐标
     * @param y 粒子簇中心点的y坐标
     * @param z 粒子簇中心点的z坐标
     * @param solidExpr 定义粒子簇形状的数学表达式。使用隐函数定义，表达式≤0的区域会生成粒子
     * @param step 采样步长，控制粒子密度
     * @param life 粒子生命周期
     * @param r 粒子红色分量 (0-1)
     * @param g 粒子绿色分量 (0-1)
     * @param b 粒子蓝色分量 (0-1)
     * @return 生成的粒子簇对象
     */
    public static ParticleCluster spawn(World w, double x, double y, double z,
                                        String solidExpr, double step,
                                        int life, float r, float g, float b) {
        // 创建粒子簇容器
        ParticleCluster c = new ParticleCluster(x, y, z);
        // 创建表达式解析器
        ExpressionParser p = new ExpressionParser();
        // 编译表达式为抽象语法树
        ExprNode ast = p.compile(solidExpr);
        // 创建变量映射表
        Map<String, Double> variables = new HashMap<>();

        // 在[-1,1]的立方体空间内采样
        for (double i = -1; i <= 1; i += step)
            for (double j = -1; j <= 1; j += step)
                for (double k = -1; k <= 1; k += step) {
                    // 设置当前采样点的坐标变量
                    variables.put("x", i);
                    variables.put("y", j);
                    variables.put("z", k);
                    // 判断采样点是否在表达式定义的形状内
                    if (ast.evaluate(variables) <= 0) {
                        // 创建粒子簇成员并添加到簇中
                        ParticleClusterMember m = new ParticleClusterMember(
                                w, x, y, z,
                                i, j, k,
                                r, g, b, 1f,
                                0.05f, life, c);
                        c.add(m);
                    }
                }
        // 渲染粒子簇
        c.spawn();
        return c;
    }
}