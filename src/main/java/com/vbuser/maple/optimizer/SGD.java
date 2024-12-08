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
            if (param.getGradient() != null) {
                for (int i = 0; i < param.data.length; i++) {
                    param.data[i] -= learningRate * param.getGradient().data[i];
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

