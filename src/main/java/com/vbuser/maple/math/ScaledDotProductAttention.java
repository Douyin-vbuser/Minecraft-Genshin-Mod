package com.vbuser.maple.math;

import com.vbuser.maple.Maple;

@SuppressWarnings("unused")
public class ScaledDotProductAttention {
    public static AutogradTensor forward(AutogradTensor Q, AutogradTensor K, AutogradTensor V, AutogradTensor mask) {
        AutogradTensor scores = Maple.matmul(Q, Maple.transpose(K, -2, -1));
        double dK = K.shape[K.shape.length - 1];
        scores = Maple.div(scores, Math.sqrt(dK));
        if (mask != null) {
            scores = Maple.add(scores, mask);
        }
        AutogradTensor attentionWeights = Maple.softmax(scores, -1);
        return Maple.matmul(attentionWeights, V);
    }
}
