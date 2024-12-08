package com.vbuser.maple.loss;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;

@SuppressWarnings("unused")
public class CrossEntropyLoss implements Loss {

    @Override
    public AutogradTensor forward(AutogradTensor predictions, AutogradTensor targets) {
        double epsilon = 1e-7;

        AutogradTensor clippedPreds = new AutogradTensor(
                predictions.data.clone(),
                predictions.getShape()
        );

        for (int i = 0; i < clippedPreds.data.length; i++) {
            clippedPreds.data[i] = Math.max(epsilon,
                    Math.min(1 - epsilon, clippedPreds.data[i]));
        }
        AutogradTensor logPreds = clippedPreds.log();

        AutogradTensor product = Maple.mul(targets, logPreds);

        AutogradTensor sum = Maple.sum(product);

        return Maple.mul(
                sum,
                new AutogradTensor(new double[]{-1.0 / targets.data.length}, new int[]{1})
        );
    }
}
