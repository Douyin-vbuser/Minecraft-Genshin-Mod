package com.vbuser.particulate.math;

public class ExpressionList {

    public static String[] leave_fall = new String[]{
            "A * sin(w_x * t + phi_x) + v_wx * t",
            "0 - 1 / 2 * g * t^0.8",
            "B * sin(w_z * t + phi_z) + v_wz * t"
    };

    public static double[] get_leave_pos(double time, ExpressionParser parser) {
        parser.setVariable("t", time);
        return new double[]{
                parser.parse(leave_fall[0]),        //x
                parser.parse(leave_fall[1]),        //y
                parser.parse(leave_fall[2])         //z
        };
    }
}
