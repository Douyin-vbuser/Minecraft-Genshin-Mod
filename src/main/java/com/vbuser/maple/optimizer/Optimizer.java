package com.vbuser.maple.optimizer;

import com.vbuser.maple.math.AutogradTensor;

@SuppressWarnings("unused")
public interface Optimizer {
    void step(AutogradTensor... parameters);

    void zeroGrad(AutogradTensor... parameters);
}
