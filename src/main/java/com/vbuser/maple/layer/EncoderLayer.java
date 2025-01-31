package com.vbuser.maple.layer;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.math.LayerNorm;
import com.vbuser.maple.math.MultiHeadAttention;
import com.vbuser.maple.nn.FFN;

import java.util.Arrays;

@SuppressWarnings("unused")
public class EncoderLayer {
    MultiHeadAttention mha;
    FFN ffn;
    LayerNorm ln1, ln2;

    public EncoderLayer(int dModel, int numHeads, int dFF) {
        this.mha = new MultiHeadAttention(dModel, numHeads);
        this.ffn = new FFN(new int[]{dModel, dFF});
        this.ln1 = new LayerNorm(dModel,1e-5);
        this.ln2 = new LayerNorm(dModel,1e-5);
    }

    public AutogradTensor forward(AutogradTensor x) {
        AutogradTensor attnOutput = mha.forward(x, x, x, null);
        System.out.println("attnOutput shape: " + Arrays.toString(attnOutput.shape));
        System.out.println("x shape: " + Arrays.toString(x.shape));

        if (!Arrays.equals(x.shape, attnOutput.shape)) {
            throw new IllegalArgumentException("x and attnOutput must have the same shape");
        }

        AutogradTensor out1 = ln1.forward(Maple.add(x, attnOutput));
        AutogradTensor ffnOutput = ffn.forward(out1);
        System.out.println("ffnOutput shape: " + Arrays.toString(ffnOutput.shape));

        if (!Arrays.equals(out1.shape, ffnOutput.shape)) {
            throw new IllegalArgumentException("out1 and ffnOutput must have the same shape");
        }

        AutogradTensor temp = Maple.add(out1, ffnOutput);
        return ln2.forward(temp);
    }
}
