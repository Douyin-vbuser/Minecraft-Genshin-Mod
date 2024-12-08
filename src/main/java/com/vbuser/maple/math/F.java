package com.vbuser.maple.math;

@SuppressWarnings("unused")
public class F {
    public static AutogradTensor relu(AutogradTensor input) {
        AutogradTensor result = new AutogradTensor(new double[input.data.length], input.shape);
        for (int i = 0; i < input.data.length; i++) {
            result.data[i] = Math.max(0, input.data[i]);
        }

        result.backward = () -> {
            if (input.gradient != null) {
                for (int i = 0; i < input.data.length; i++) {
                    input.gradient.data[i] += (input.data[i] > 0 ? 1 : 0) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public static AutogradTensor sigmoid(AutogradTensor input) {
        AutogradTensor result = new AutogradTensor(new double[input.data.length], input.shape);
        for (int i = 0; i < input.data.length; i++) {
            result.data[i] = 1.0 / (1.0 + Math.exp(-input.data[i]));
        }

        result.backward = () -> {
            if (input.gradient != null) {
                for (int i = 0; i < input.data.length; i++) {
                    double sigmoidX = result.data[i];
                    input.gradient.data[i] += sigmoidX * (1 - sigmoidX) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public static AutogradTensor tanh(AutogradTensor input) {
        AutogradTensor result = new AutogradTensor(new double[input.data.length], input.shape);
        for (int i = 0; i < input.data.length; i++) {
            result.data[i] = Math.tanh(input.data[i]);
        }

        result.backward = () -> {
            if (input.gradient != null) {
                for (int i = 0; i < input.data.length; i++) {
                    double tanhX = result.data[i];
                    input.gradient.data[i] += (1 - tanhX * tanhX) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public static AutogradTensor leaky_relu(AutogradTensor input, double alpha) {
        AutogradTensor result = new AutogradTensor(new double[input.data.length], input.shape);
        for (int i = 0; i < input.data.length; i++) {
            result.data[i] = input.data[i] > 0 ? input.data[i] : alpha * input.data[i];
        }

        result.backward = () -> {
            if (input.gradient != null) {
                for (int i = 0; i < input.data.length; i++) {
                    input.gradient.data[i] += (input.data[i] > 0 ? 1 : alpha) * result.gradient.data[i];
                }
            }
        };

        return result;
    }

    public static AutogradTensor softmax(AutogradTensor input) {
        AutogradTensor result = new AutogradTensor(new double[input.data.length], input.shape);

        double maxVal = Double.NEGATIVE_INFINITY;
        for (double val : input.data) {
            maxVal = Math.max(maxVal, val);
        }

        double sum = 0.0;
        for (int i = 0; i < input.data.length; i++) {
            result.data[i] = Math.exp(input.data[i] - maxVal);
            sum += result.data[i];
        }

        for (int i = 0; i < input.data.length; i++) {
            result.data[i] /= sum;
        }

        result.backward = () -> {
            if (input.gradient != null) {
                for (int i = 0; i < input.data.length; i++) {
                    double si = result.data[i];
                    for (int j = 0; j < input.data.length; j++) {
                        double sj = result.data[j];
                        input.gradient.data[i] += si * ((i == j ? 1 : 0) - sj) * result.gradient.data[j];
                    }
                }
            }
        };

        return result;
    }

    public static AutogradTensor elu(AutogradTensor input, double alpha) {
        AutogradTensor result = new AutogradTensor(new double[input.data.length], input.shape);
        for (int i = 0; i < input.data.length; i++) {
            result.data[i] = input.data[i] > 0 ?
                    input.data[i] : alpha * (Math.exp(input.data[i]) - 1);
        }

        result.backward = () -> {
            if (input.gradient != null) {
                for (int i = 0; i < input.data.length; i++) {
                    double derivative = input.data[i] > 0 ?
                            1 : alpha * Math.exp(input.data[i]);
                    input.gradient.data[i] += derivative * result.gradient.data[i];
                }
            }
        };

        return result;
    }
}
