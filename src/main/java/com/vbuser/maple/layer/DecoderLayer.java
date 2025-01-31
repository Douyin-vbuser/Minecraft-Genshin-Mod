package com.vbuser.maple.layer;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.math.LayerNorm;
import com.vbuser.maple.math.MultiHeadAttention;
import com.vbuser.maple.nn.FFN;

@SuppressWarnings("unused")
public class DecoderLayer {

    MultiHeadAttention selfAttn, encDecAttn;
    FFN ffn;
    LayerNorm ln1, ln2, ln3;

    public DecoderLayer(int dModel, int numHeads, int dFF) {
        this.selfAttn = new MultiHeadAttention(dModel, numHeads);
        this.encDecAttn = new MultiHeadAttention(dModel, numHeads);
        this.ffn = new FFN(new int[]{dModel, dFF});
        this.ln1 = new LayerNorm(dModel,1e-5);
        this.ln2 = new LayerNorm(dModel,1e-5);
        this.ln3 = new LayerNorm(dModel,1e-5);
    }

    public AutogradTensor forward(AutogradTensor x, AutogradTensor encOutput) {
        AutogradTensor selfAttnOutput = selfAttn.forward(x, x, x,null);
        AutogradTensor out1 = ln1.forward(Maple.add(x, selfAttnOutput));

        AutogradTensor encDecAttnOutput = encDecAttn.forward(out1, encOutput, encOutput,null);
        AutogradTensor out2 = ln2.forward(Maple.add(out1, encDecAttnOutput));

        AutogradTensor ffnOutput = ffn.forward(out2);
        return ln3.forward(Maple.add(out2, ffnOutput));
    }
}
