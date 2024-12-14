package com.vbuser.maple.loss;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;

@SuppressWarnings("unused")
public class MSELoss implements Loss {
    @Override
    public AutogradTensor forward(AutogradTensor predictions, AutogradTensor targets) {
        AutogradTensor diff = Maple.sub(predictions,targets);
        AutogradTensor squared = Maple.mul(diff,diff);
        return Maple.mean(squared);
    }
}
