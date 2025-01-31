package com.vbuser.maple.math;

import com.vbuser.maple.Maple;
import com.vbuser.maple.layer.Linear;

import java.util.Arrays;

@SuppressWarnings("unused")
public class MultiHeadAttention {
    Linear WQ, WK, WV;
    Linear WO;
    int numHeads;
    int dModel;
    int dK;

    public MultiHeadAttention(int dModel, int numHeads) {
        this.dModel = dModel;
        this.numHeads = numHeads;
        this.dK = dModel / numHeads;

        if (dModel % numHeads != 0) {
            throw new IllegalArgumentException("dModel must be divisible by numHeads");
        }

        this.WQ = new Linear(dModel, dModel);
        this.WK = new Linear(dModel, dModel);
        this.WV = new Linear(dModel, dModel);
        this.WO = new Linear(dModel, dModel);
    }

    public AutogradTensor forward(AutogradTensor Q, AutogradTensor K, AutogradTensor V, AutogradTensor mask) {
        int batchSize = Q.shape[0];
        int seqLen = Q.shape[1];
        AutogradTensor Q_proj = WQ.forward(Q);  // [batchSize, seqLen, dModel]
        AutogradTensor K_proj = WK.forward(K);  // [batchSize, seqLen, dModel]
        AutogradTensor V_proj = WV.forward(V);  // [batchSize, seqLen, dModel]
        System.out.println("Q_proj shape: " + Arrays.toString(Q_proj.shape));
        System.out.println("K_proj shape: " + Arrays.toString(K_proj.shape));
        System.out.println("V_proj shape: " + Arrays.toString(V_proj.shape));

        AutogradTensor Q_split = splitHeads(Q_proj);  // [batchSize, numHeads, seqLen, dK]
        AutogradTensor K_split = splitHeads(K_proj);  // [batchSize, numHeads, seqLen, dK]
        AutogradTensor V_split = splitHeads(V_proj);  // [batchSize, numHeads, seqLen, dK]
        System.out.println("Q_split shape: " + Arrays.toString(Q_split.shape));
        System.out.println("K_split shape: " + Arrays.toString(K_split.shape));
        System.out.println("V_split shape: " + Arrays.toString(V_split.shape));

        AutogradTensor attentionOutput = ScaledDotProductAttention.forward(Q_split, K_split, V_split, mask);
        System.out.println("attentionOutput shape: " + Arrays.toString(attentionOutput.shape));

        AutogradTensor concatOutput = concatHeads(attentionOutput);  // [batchSize, seqLen, dModel]
        System.out.println("concatOutput shape: " + Arrays.toString(concatOutput.shape));

        return WO.forward(concatOutput);
    }

    private AutogradTensor splitHeads(AutogradTensor x) {
        int batchSize = x.shape[0];
        int seqLen = x.shape[1];
        if (x.shape.length != 3 || x.shape[2] != dModel) {
            throw new IllegalArgumentException("Input tensor must have shape [batchSize, seqLen, dModel]");
        }
        AutogradTensor reshaped = x.reshape(batchSize, seqLen, numHeads, dK);
        return Maple.transpose(reshaped, 1, 2);
    }

    private AutogradTensor concatHeads(AutogradTensor x) {
        int batchSize = x.shape[0];
        int numHeads = x.shape[1];
        int seqLen = x.shape[2];
        int dK = x.shape[3];

        AutogradTensor transposed = Maple.transpose(x, 1, 2);
        return transposed.reshape(batchSize, seqLen, numHeads * dK);
    }
}