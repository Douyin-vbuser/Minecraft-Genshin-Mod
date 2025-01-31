package com.vbuser.maple.nn;

import com.vbuser.maple.layer.DecoderLayer;
import com.vbuser.maple.layer.EncoderLayer;
import com.vbuser.maple.layer.Linear;
import com.vbuser.maple.math.AutogradTensor;

import java.util.Arrays;

@SuppressWarnings("unused")
public class Transformer {
    EncoderLayer[] encoders;
    DecoderLayer[] decoders;
    Linear fcOut;

    public Transformer(int numLayers, int dModel, int numHeads, int dFF, int vocabSize) {
        this.encoders = new EncoderLayer[numLayers];
        this.decoders = new DecoderLayer[numLayers];
        for (int i = 0; i < numLayers; i++) {
            encoders[i] = new EncoderLayer(dModel, numHeads, dFF);
            decoders[i] = new DecoderLayer(dModel, numHeads, dFF);
        }
        this.fcOut = new Linear(dModel, vocabSize);
    }

    public AutogradTensor forward(AutogradTensor src, AutogradTensor tgt) {
        AutogradTensor encOutput = src;
        for (EncoderLayer encoder : encoders) {
            encOutput = encoder.forward(encOutput);
        }

        AutogradTensor decOutput = tgt;
        for (DecoderLayer decoder : decoders) {
            decOutput = decoder.forward(decOutput, encOutput);
        }

        return fcOut.forward(decOutput);
    }

    public static void main(String[] args) {
        int numLayers = 2;
        int dModel = 64;
        int numHeads = 8;
        int dFF = 128;
        int vocabSize = 10000;
        Transformer transformer = new Transformer(numLayers, dModel, numHeads, dFF, vocabSize);
        int batchSize = 2;
        int seqLen = 10;

        AutogradTensor src = AutogradTensor.randn(batchSize, seqLen, dModel);
        AutogradTensor tgt = AutogradTensor.randn(batchSize, seqLen, dModel);
        AutogradTensor output = transformer.forward(src, tgt);
        System.out.println("Output shape: " + Arrays.toString(output.shape));
    }
}
