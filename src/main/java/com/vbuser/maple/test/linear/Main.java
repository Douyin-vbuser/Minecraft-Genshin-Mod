package com.vbuser.maple.test.linear;

import com.vbuser.maple.loss.Loss;
import com.vbuser.maple.loss.MSELoss;
import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.optimizer.Optimizer;
import com.vbuser.maple.optimizer.SGD;

public class Main {
    public static void main(String[] args) {
        AutogradTensor inputs = new AutogradTensor(new double[]{1, 2, 3, 4, 5 }, new int[]{5, 1});
        AutogradTensor targets = new AutogradTensor(new double[]{2, 4, 6, 8, 10 }, new int[]{5, 1});

        Optimizer optimizer = new SGD(0.001);
        Loss lossFunction = new MSELoss();

        LinearRegression model = new LinearRegression(1, optimizer, lossFunction);

        model.train(inputs, targets, 10000, 2);

        AutogradTensor testInput = new AutogradTensor(new double[]{6}, new int[]{1, 1});
        AutogradTensor prediction = model.predict(testInput);
        System.out.println("Prediction for input 6: " + prediction.data[0]);
    }
}
