package com.vbuser.maple.test.linear;

import com.vbuser.maple.Maple;
import com.vbuser.maple.loss.Loss;
import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.optimizer.Optimizer;

public class LinearRegression {
    AutogradTensor weights;
    AutogradTensor bias;
    Optimizer optimizer;
    Loss lossFunction;

    public LinearRegression(int inputSize, Optimizer optimizer, Loss lossFunction) {
        this.weights = new AutogradTensor(new double[inputSize], new int[]{inputSize, 1});
        this.bias = new AutogradTensor(new double[]{0}, new int[]{1});
        this.optimizer = optimizer;
        this.lossFunction = lossFunction;

        for (int i = 0; i < inputSize; i++) {
            this.weights.data[i] = Math.random() - 0.5;
        }
    }

    public AutogradTensor forward(AutogradTensor input) {
        return Maple.add(Maple.mul(input, weights), bias);
    }

    public void train(AutogradTensor inputs, AutogradTensor targets, int epochs, int batchSize) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < inputs.shape[0]; i += batchSize) {
                int endIndex = Math.min(i + batchSize, inputs.shape[0]);
                AutogradTensor batchInputs = inputs.slice(i, endIndex);
                AutogradTensor batchTargets = targets.slice(i, endIndex);

                AutogradTensor predictions = forward(batchInputs);

                AutogradTensor loss = lossFunction.forward(predictions, batchTargets);

                optimizer.zeroGrad(weights, bias);
                loss.backward();

                optimizer.step(weights, bias);

                if (i % 100 == 0) {
                    System.out.println("Epoch " + epoch + ", Batch " + i + ", Loss: " + loss.data[0]);
                }
            }
        }
    }

    public AutogradTensor predict(AutogradTensor input) {
        return forward(input);
    }
}