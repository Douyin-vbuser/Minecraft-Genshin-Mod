package com.vbuser.maple.nn;

import com.vbuser.maple.math.AutogradTensor;

public abstract class Module {
    public abstract AutogradTensor forward(AutogradTensor input);
}
