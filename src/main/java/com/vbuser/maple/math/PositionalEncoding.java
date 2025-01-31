package com.vbuser.maple.math;

import com.vbuser.maple.Maple;

@SuppressWarnings("unused")
public class PositionalEncoding {
    AutogradTensor pe;

    public PositionalEncoding(int maxLen, int dModel) {
        double[][] pe = new double[maxLen][dModel];
        for (int pos = 0; pos < maxLen; pos++) {
            for (int i = 0; i < dModel; i++) {
                double angle = pos / Math.pow(10000, (double) (2 * i) / dModel);
                pe[pos][i] = (i % 2 == 0) ? Math.sin(angle) : Math.cos(angle);
            }
        }
        this.pe = Maple.fromArray(pe);
    }

    public AutogradTensor forward(AutogradTensor x) {
        return Maple.add(x, pe.slice(0, x.shape[0]));
    }
}
