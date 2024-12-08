package com.vbuser.maple.loss;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;

@SuppressWarnings("unused")
public class BinaryCrossEntropyLoss implements Loss {
    private static final double EPSILON = 1e-7;

    @Override
    public AutogradTensor forward(AutogradTensor predictions, AutogradTensor targets) {
        int batchSize = predictions.shape[0];
        AutogradTensor loss = new AutogradTensor(new double[batchSize], new int[]{batchSize});

        for (int i = 0; i < batchSize; i++) {
            double p = Math.max(Math.min(predictions.data[i], 1 - EPSILON), EPSILON);
            double t = targets.data[i];
            loss.data[i] = -t * Math.log(p) - (1 - t) * Math.log(1 - p);
        }

        return Maple.mean(loss);
    }
}
