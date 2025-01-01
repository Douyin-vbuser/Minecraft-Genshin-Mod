package com.vbuser.maple.layer;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;

import java.util.Arrays;

@SuppressWarnings("unused")
public class Linear {
    public AutogradTensor weights;
    public AutogradTensor bias;

    public Linear(int inputSize, int outputSize) {
        this.weights = new AutogradTensor(new double[inputSize * outputSize], new int[]{inputSize, outputSize});
        for (int i = 0; i < inputSize * outputSize; i++) {
            this.weights.data[i] = Math.random() - 0.5;
        }

        this.bias = new AutogradTensor(new double[outputSize], new int[]{outputSize});
        Arrays.fill(this.bias.data, 0.0);
    }

    public AutogradTensor forward(AutogradTensor input) {
        return Maple.add(Maple.matmul(input, weights), bias);
    }
}
