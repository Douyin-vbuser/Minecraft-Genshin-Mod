package com.vbuser.maple.optimizer;

import com.vbuser.maple.math.AutogradTensor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class Adam implements Optimizer {
    private final double learningRate;
    private final double beta1;
    private final double beta2;
    private final double epsilon;
    private final Map<AutogradTensor, double[]> m;
    private final Map<AutogradTensor, double[]> v;
    private int t;

    public Adam(double learningRate, double beta1, double beta2, double epsilon) {
        this.learningRate = learningRate;
        this.beta1 = beta1;
        this.beta2 = beta2;
        this.epsilon = epsilon;
        this.m = new HashMap<>();
        this.v = new HashMap<>();
        this.t = 0;
    }

    @Override
    public void step(AutogradTensor... parameters) {
        t++;
        for (AutogradTensor param : parameters) {
            if (param.getGradient() != null) {
                if (!m.containsKey(param)) {
                    m.put(param, new double[param.data.length]);
                    v.put(param, new double[param.data.length]);
                }
                double[] mParam = m.get(param);
                double[] vParam = v.get(param);

                for (int i = 0; i < param.data.length; i++) {
                    double grad = param.getGradient().data[i];
                    mParam[i] = beta1 * mParam[i] + (1 - beta1) * grad;
                    vParam[i] = beta2 * vParam[i] + (1 - beta2) * grad * grad;
                    double mHat = mParam[i] / (1 - Math.pow(beta1, t));
                    double vHat = vParam[i] / (1 - Math.pow(beta2, t));
                    param.data[i] -= learningRate * mHat / (Math.sqrt(vHat) + epsilon);
                }
            }
        }
    }

    @Override
    public void zeroGrad(AutogradTensor... parameters) {
        for (AutogradTensor param : parameters) {
            if (param.getGradient() != null) {
                Arrays.fill(param.getGradient().data, 0.0);
            }
        }
    }
}