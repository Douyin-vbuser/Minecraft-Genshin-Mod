package com.vbuser.particulate.math;

public class ExpressionList {

    public static String[] leave_fall = new String[] {
            "A * sin(w_x * t + phi_x) + v_wx * t",
            "0 - 1 / 2 * g * t^0.8",
            "B * sin(w_z * t + phi_z) + v_wz * t"
    };

    public static double[] get_leave_pos(double time){
        ExpressionParser parser = new ExpressionParser();
        parser.setVariable("t", time);
        parser.setVariable("A",0.4);
        parser.setVariable("w_x",0.2);
        parser.setVariable("v_wx",2);
        parser.setVariable("g",0.8);
        parser.setVariable("B",0.4);
        parser.setVariable("w_z",0.2);
        parser.setVariable("v_wz",2);
        parser.setVariable("phi_z",0);
        parser.setVariable("phi_x",0);
        return new double[] {
                parser.parse(leave_fall[0]),        //x
                parser.parse(leave_fall[1]),        //y
                parser.parse(leave_fall[2])         //z
        };
    }
}
