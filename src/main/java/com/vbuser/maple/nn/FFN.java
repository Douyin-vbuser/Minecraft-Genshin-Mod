package com.vbuser.maple.nn;

import com.vbuser.maple.layer.Linear;
import com.vbuser.maple.math.AutogradTensor;

public class FFN {
    Linear[] layers;

    public FFN(int[] layerSizes) {
        if (layerSizes.length < 2) {
            throw new IllegalArgumentException("At least two layers are required (input and output)");
        }

        this.layers = new Linear[layerSizes.length - 1];
        for (int i = 0; i < layers.length; i++) {
            if (i == layers.length - 1) {
                layers[i] = new Linear(layerSizes[i], layerSizes[0]);
            } else {
                layers[i] = new Linear(layerSizes[i], layerSizes[i + 1]);
            }
        }
    }

    public AutogradTensor forward(AutogradTensor input) {
        AutogradTensor output = input;

        for (int i = 0; i < layers.length - 1; i++) {
            output = layers[i].forward(output);
            output = output.relu();
        }

        output = layers[layers.length - 1].forward(output);

        return output;
    }
}
