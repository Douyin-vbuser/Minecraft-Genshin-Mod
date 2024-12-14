package com.vbuser.maple.loss;

import com.vbuser.maple.math.AutogradTensor;

@SuppressWarnings("unused")
public interface Loss {
    AutogradTensor forward(AutogradTensor predictions, AutogradTensor targets);
}
