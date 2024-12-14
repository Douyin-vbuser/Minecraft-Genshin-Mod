package com.vbuser.maple.optimizer;

import com.vbuser.maple.math.AutogradTensor;

import java.util.Arrays;

@SuppressWarnings("unused")
public class SGD implements Optimizer {
    private final double learningRate;

    public SGD(double learningRate) {
        this.learningRate = learningRate;
    }

    @Override
    public void step(AutogradTensor... parameters) {
        for (AutogradTensor param : parameters) {
            if (param.gradient != null) {
                for (int i = 0; i < param.data.length; i++) {
                    param.data[i] -= learningRate * param.gradient[i];
                }
            }
        }
    }

    @Override
    public void zeroGrad(AutogradTensor... parameters) {
        for (AutogradTensor param : parameters) {
            if (param.gradient != null) {
                Arrays.fill(param.gradient, 0.0);
            }
        }
    }
}
