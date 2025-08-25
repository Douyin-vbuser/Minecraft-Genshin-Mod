package com.vbuser.particulate.math;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ExpressionList {
    public static String[] leave_fall = new String[]{
            "A * sin(w_x * t + phi_x) + v_wx * t",
            "0 - 1 / 2 * g * t^0.8",
            "B * sin(w_z * t + phi_z) + v_wz * t"
    };

    private static final Map<String, ExprNode[]> astCache = new HashMap<>();

    public static ExprNode[] get_leave_pos_ast() {
        return astCache.computeIfAbsent("leave_fall", k -> {
            ExpressionParser parser = new ExpressionParser();
            ExprNode[] asts = new ExprNode[3];
            for (int i = 0; i < 3; i++) {
                asts[i] = parser.compile(leave_fall[i]);
            }
            return asts;
        });
    }

    public static double[] get_leave_pos(double time, Map<String, Double> variables) {
        variables.put("t", time);
        ExprNode[] asts = get_leave_pos_ast();
        return new double[]{
                asts[0].evaluate(variables),
                asts[1].evaluate(variables),
                asts[2].evaluate(variables)
        };
    }
}