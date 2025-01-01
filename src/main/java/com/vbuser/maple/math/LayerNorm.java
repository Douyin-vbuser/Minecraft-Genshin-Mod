package com.vbuser.maple.math;

import com.vbuser.maple.Maple;

@SuppressWarnings("unused")
public class LayerNorm {
    public int featureSize;
    public double epsilon;
    public AutogradTensor gamma;
    public AutogradTensor beta;

    public LayerNorm(int featureSize, double epsilon) {
        this.featureSize = featureSize;
        this.epsilon = epsilon;
        this.gamma = Maple.ones(featureSize);
        this.beta = Maple.zeros(featureSize);
    }

    public AutogradTensor forward(AutogradTensor input) {
        int[] lastDim = {input.shape.length - 1};
        AutogradTensor mean = Maple.mean(input, lastDim);
        int[] newShape = new int[input.shape.length];
        if (input.shape.length - 1 >= 0) System.arraycopy(input.shape, 0, newShape, 0, input.shape.length - 1);
        newShape[input.shape.length - 1] = 1;
        mean = mean.reshape(newShape);

        AutogradTensor variance = Maple.mean(Maple.pow(Maple.sub(input, mean), 2), lastDim);
        variance = variance.reshape(newShape);

        AutogradTensor normalized = Maple.div(Maple.sub(input, mean),
                Maple.pow(Maple.add(variance, epsilon), 0.5));

        return Maple.add(Maple.mul(normalized, gamma), beta);
    }

    public AutogradTensor getGamma() {
        return gamma;
    }

    public AutogradTensor getBeta() {
        return beta;
    }

    public static void main(String[] args) {
        LayerNorm layerNorm = new LayerNorm(4, 1e-5);

        double[][] inputData = {{1, 2, 3, 4}, {5, 6, 7, 8}};
        AutogradTensor input = Maple.fromArray(inputData);

        AutogradTensor output = layerNorm.forward(input);

        System.out.println("Input:");
        System.out.println(input);

        System.out.println("Output:");
        System.out.println(output);

        output.backward();

        System.out.println("Input Gradient:");
        System.out.println(java.util.Arrays.toString(input.gradient));

        System.out.println("Gamma Gradient:");
        System.out.println(java.util.Arrays.toString(layerNorm.getGamma().gradient));

        System.out.println("Beta Gradient:");
        System.out.println(java.util.Arrays.toString(layerNorm.getBeta().gradient));
    }
}