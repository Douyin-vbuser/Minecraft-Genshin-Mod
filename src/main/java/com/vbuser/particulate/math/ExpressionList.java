//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:DeepSeek
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.math;

import java.util.HashMap;
import java.util.Map;

/**
 * ExpressionList 类提供预定义的数学表达式及其计算功能<br>
 * 包含叶子下落动画的数学表达式，并提供表达式编译和计算的方法
 */
@SuppressWarnings("unused")
public class ExpressionList {

    /**
     * 叶子下落动画的三维位置表达式数组<br>
     * 分别对应 x、y、z 轴的位置计算公式
     */
    public static String[] leave_fall = new String[]{
            "A * sin(w_x * t + phi_x) + v_wx * t",  // x轴位置公式：正弦运动加线性运动
            "0 - 1 / 2 * g * t^0.8",                // y轴位置公式：重力加速度影响的运动
            "B * sin(w_z * t + phi_z) + v_wz * t"   // z轴位置公式：正弦运动加线性运动
    };

    /**
     * 抽象语法树缓存，用于存储已编译的表达式<br>
     * 键为表达式名称，值为对应的已编译表达式节点数组
     */
    private static final Map<String, ExprNode[]> astCache = new HashMap<>();

    /**
     * 获取叶子下落位置的已编译表达式抽象语法树<br>
     * 使用缓存机制避免重复编译相同表达式
     * @return 包含三个坐标轴表达式节点的数组
     */
    public static ExprNode[] get_leave_pos_ast() {
        return astCache.computeIfAbsent("leave_fall", k -> {
            ExpressionParser parser = new ExpressionParser();
            ExprNode[] asts = new ExprNode[3];
            for (int i = 0; i < 3; i++) {
                // 编译每个坐标轴的表达式
                asts[i] = parser.compile(leave_fall[i]);
            }
            return asts;
        });
    }

    /**
     * 根据时间计算叶子在三维空间中的位置<br>
     * 使用预定义的表达式和提供的变量值进行计算
     * @param time 时间参数，通常表示动画的进行时间
     * @param variables 变量映射表，包含表达式计算所需的所有变量值
     * @return 包含三个坐标值的数组 [x, y, z]
     */
    public static double[] get_leave_pos(double time, Map<String, Double> variables) {
        // 将时间变量添加到变量映射表中
        variables.put("t", time);
        // 获取已编译的表达式抽象语法树
        ExprNode[] asts = get_leave_pos_ast();
        // 计算并返回三个坐标轴的值
        return new double[]{
                asts[0].evaluate(variables),  // 计算x坐标
                asts[1].evaluate(variables),  // 计算y坐标
                asts[2].evaluate(variables)   // 计算z坐标
        };
    }
}