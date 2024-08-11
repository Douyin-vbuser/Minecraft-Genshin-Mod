package com.vbuser.particulate.util;

public class ExpressionList {
    public static String[] leave_fall = new String[] {
            "A1 * sin(w1 * t) + A2 * sin(w2 * t) + v_x * t + W_x * sin(w_wt)",
            "B1 * sin(w3 * t) + B2 * sin(w4 * t) + v_y * t + W_y * sin(w_wt)",
            "H - (g * t^2 / 2) * (1 - e^(0 - k_zt)) + C1 * sin(w5 * t) * e^(0 - k_zt) + C2 * cos(w6 * t) * e^(0 - k_zt)",
            "sita0 + ¦Ø_sita * t + D * sin(¦Ø_sita * t)"
    };

    public static double[] get_leave_pos(double time){
        ExpressionParser parser = new ExpressionParser();
        parser.setVariable("A1" , Math.random() * 0.5);
        parser.setVariable("A2", Math.random() * 0.3);
        parser.setVariable("B1", Math.random() * 0.4);
        parser.setVariable("B2", Math.random() * 0.2);
        parser.setVariable("C1", Math.random() * 0.2);
        parser.setVariable("C2", Math.random() * 0.1);
        parser.setVariable("w1", 2.0 + Math.random());
        parser.setVariable("w2", 3.0 + Math.random());
        parser.setVariable("w3", 2.5 + Math.random());
        parser.setVariable("w4", 3.5 + Math.random());
        parser.setVariable("w5", 4.0 + Math.random());
        parser.setVariable("w6", 5.0 + Math.random());
        parser.setVariable("v_x", (Math.random() - 0.5) * 0.4);
        parser.setVariable("v_y", (Math.random() - 0.5) * 0.2);
        parser.setVariable("W_x", Math.random() * 0.3);
        parser.setVariable("W_y", Math.random() * 0.2);
        parser.setVariable("w_w", 0.5 + Math.random() * 0.5);
        parser.setVariable("H",10);
        parser.setVariable("k_z", 0.1 + Math.random() * 0.1);
        parser.setVariable("w_sita", Math.random() * 2);
        parser.setVariable("D", Math.random() * Math.PI / 4);
        parser.setVariable("t" , time);
        return new double[] {
                parser.parse(leave_fall[0]),
                parser.parse(leave_fall[2]),
                parser.parse(leave_fall[1])
        };
    }
}
