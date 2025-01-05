package com.vbuser.maple.nn;

import com.vbuser.maple.layer.Linear;
import com.vbuser.maple.loss.Loss;
import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.optimizer.Optimizer;

@SuppressWarnings("unused")
public class LinearNet extends Module {
    public Linear linearLayer;
    public Optimizer optimizer;
    public Loss lossFunction;
    boolean output;

    public LinearNet(int inputSize, int outputSize, Optimizer optimizer, Loss lossFunction, boolean output) {
        this.linearLayer = new Linear(inputSize, outputSize);
        this.optimizer = optimizer;
        this.lossFunction = lossFunction;
        this.output = output;
    }

    public LinearNet(int inputSize, int outputSize, Optimizer optimizer, Loss lossFunction) {
        this.linearLayer = new Linear(inputSize, outputSize);
        this.optimizer = optimizer;
        this.lossFunction = lossFunction;
        this.output = false;
    }

    @Override
    public AutogradTensor forward(AutogradTensor input) {
        return linearLayer.forward(input);
    }

    public void train(AutogradTensor inputs, AutogradTensor targets, int epochs, int batchSize) {
        for (int epoch = 0; epoch < epochs; epoch++) {
            for (int i = 0; i < inputs.shape[0]; i += batchSize) {
                int endIndex = Math.min(i + batchSize, inputs.shape[0]);
                AutogradTensor batchInputs = inputs.slice(i, endIndex);
                AutogradTensor batchTargets = targets.slice(i, endIndex);

                AutogradTensor predictions = forward(batchInputs);

                AutogradTensor loss = lossFunction.forward(predictions, batchTargets);

                optimizer.zeroGrad(linearLayer.weights, linearLayer.bias);
                loss.backward();

                optimizer.step(linearLayer.weights, linearLayer.bias);

                if (i % 100 == 0 && output) {
                    System.out.println("Epoch " + epoch + ", Batch " + i + ", Loss: " + loss.data[0]);
                }
            }
        }
    }

    public AutogradTensor predict(AutogradTensor input) {
        return forward(input);
    }
}
