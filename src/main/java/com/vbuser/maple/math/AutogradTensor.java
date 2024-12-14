package com.vbuser.maple.math;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

@SuppressWarnings("unused")
public class AutogradTensor extends Tensor {
    public double[] gradient;

    private Operation op;

    public AutogradTensor(double[] data, int[] shape) {
        super(data, shape);
        this.gradient = new double[data.length];
        this.op = null;
    }

    public AutogradTensor(int... shape) {
        super(shape);
        this.gradient = new double[this.data.length];
        this.op = null;
    }

    public double[] getGrad() {
        return gradient;
    }

    public void zeroGrad() {
        Arrays.fill(gradient, 0.0);
    }

    public AutogradTensor getGradAsTensor() {
        return new AutogradTensor(getGrad(), shape);
    }

    private static int[] broadcastShapes(int[] shape1, int[] shape2) {
        int maxDims = Math.max(shape1.length, shape2.length);
        int[] result = new int[maxDims];
        for (int i = 0; i < maxDims; i++) {
            int dim1 = i < shape1.length ? shape1[shape1.length - 1 - i] : 1;
            int dim2 = i < shape2.length ? shape2[shape2.length - 1 - i] : 1;
            result[maxDims - 1 - i] = Math.max(dim1, dim2);
        }
        return result;
    }

    public int[] getIndices(int flatIndex) {
        return getIndices(flatIndex, this.shape);
    }

    public AutogradTensor slice(int start, int end) {
        if (start < 0 || end > this.data.length || start >= end) {
            throw new IllegalArgumentException("Invalid slice range");
        }

        int sliceLength = end - start;
        double[] slicedData = Arrays.copyOfRange(this.data, start, end);

        int[] newShape;
        if (this.shape.length > 1) {
            newShape = Arrays.copyOf(this.shape, this.shape.length);
            newShape[0] = sliceLength / (this.data.length / this.shape[0]);
        } else {
            newShape = new int[]{sliceLength};
        }

        return new AutogradTensor(slicedData, newShape);
    }

    public static int[] getIndices(int flatIndex, int[] shape) {
        int[] indices = new int[shape.length];
        for (int i = shape.length - 1; i >= 0; i--) {
            indices[i] = flatIndex % shape[i];
            flatIndex /= shape[i];
        }
        return indices;
    }

    public int[] getBroadcastIndices(int flatIndex, int[] broadcastShape) {
        int[] broadcastIndices = getIndices(flatIndex, broadcastShape);
        int[] indices = new int[this.shape.length];

        for (int i = 0; i < this.shape.length; i++) {
            int broadcastDim = broadcastShape[broadcastShape.length - this.shape.length + i];
            int thisDim = this.shape[i];

            if (thisDim == broadcastDim) {
                indices[i] = broadcastIndices[broadcastShape.length - this.shape.length + i];
            } else if (thisDim == 1) {
                indices[i] = 0;
            } else {
                throw new IllegalArgumentException("Invalid broadcast");
            }
        }

        return indices;
    }

    public AutogradTensor add(AutogradTensor other) {
        int[] resultShape = broadcastShapes(this.shape, other.shape);
        AutogradTensor result = new AutogradTensor(resultShape);

        for (int i = 0; i < result.data.length; i++) {
            int[] broadcastIndices = getIndices(i, resultShape);
            int[] thisIndices = this.getBroadcastIndices(i, resultShape);
            int[] otherIndices = other.getBroadcastIndices(i, resultShape);
            result.data[i] = getElement(thisIndices) + other.getElement(otherIndices);
        }

        result.op = new Operation(result, this, other, "add");
        return result;
    }

    public AutogradTensor subtract(AutogradTensor other) {
        int[] resultShape = broadcastShapes(this.shape, other.shape);
        AutogradTensor result = new AutogradTensor(resultShape);

        for (int i = 0; i < result.data.length; i++) {
            int[] broadcastIndices = getIndices(i, resultShape);
            int[] thisIndices = this.getBroadcastIndices(i, resultShape);
            int[] otherIndices = other.getBroadcastIndices(i, resultShape);
            result.data[i] = getElement(thisIndices) - other.getElement(otherIndices);
        }

        result.op = new Operation(result, this, other, "subtract");
        return result;
    }

    public AutogradTensor multiply(AutogradTensor other) {
        int[] resultShape = broadcastShapes(this.shape, other.shape);
        AutogradTensor result = new AutogradTensor(resultShape);

        for (int i = 0; i < result.data.length; i++) {
            int[] broadcastIndices = getIndices(i, resultShape);
            int[] thisIndices = this.getBroadcastIndices(i, resultShape);
            int[] otherIndices = other.getBroadcastIndices(i, resultShape);
            result.data[i] = getElement(thisIndices) * other.getElement(otherIndices);
        }

        result.op = new Operation(result, this, other, "multiply");
        return result;
    }

    public AutogradTensor divide(AutogradTensor other) {
        int[] resultShape = broadcastShapes(this.shape, other.shape);
        AutogradTensor result = new AutogradTensor(resultShape);

        for (int i = 0; i < result.data.length; i++) {
            int[] broadcastIndices = getIndices(i, resultShape);
            int[] thisIndices = this.getBroadcastIndices(i, resultShape);
            int[] otherIndices = other.getBroadcastIndices(i, resultShape);
            result.data[i] = getElement(thisIndices) / other.getElement(otherIndices);
        }

        result.op = new Operation(result, this, other, "divide");
        return result;
    }

    public AutogradTensor exp() {
        AutogradTensor result = new AutogradTensor(this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.exp(this.data[i]);
        }
        result.op = new Operation(result, this, null, "exp");
        return result;
    }

    public AutogradTensor log() {
        AutogradTensor result = new AutogradTensor(this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.log(this.data[i]);
        }
        result.op = new Operation(result, this, null, "log");
        return result;
    }

    public AutogradTensor pow(double exponent) {
        AutogradTensor result = new AutogradTensor(this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.pow(this.data[i], exponent);
        }
        result.op = new Operation(result, this, new AutogradTensor(new double[]{exponent}, new int[]{1}), "pow");
        return result;
    }

    public AutogradTensor mean() {
        double sum = 0;
        for (double value : this.data) {
            sum += value;
        }
        double mean = sum / this.data.length;

        AutogradTensor result = new AutogradTensor(new double[]{mean}, new int[]{1});
        result.op = new Operation(result, this, null, "mean");
        return result;
    }

    public AutogradTensor sum() {
        double sum = 0;
        for (double value : this.data) {
            sum += value;
        }
        AutogradTensor result = new AutogradTensor(new double[]{sum}, new int[]{1});
        result.op = new Operation(result, this, null, "sum");
        return result;
    }


    public int getFlatIndex(int[] indices) {
        if (indices.length != shape.length) {
            throw new IllegalArgumentException("Indices dimension doesn't match tensor dimension");
        }

        int flatIndex = 0;
        int multiplier = 1;

        for (int i = shape.length - 1; i >= 0; i--) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new IndexOutOfBoundsException("Index out of bounds for dimension " + i);
            }
            flatIndex += indices[i] * multiplier;
            multiplier *= shape[i];
        }

        return flatIndex;
    }

    public void backward() {
        Arrays.fill(this.gradient, 1.0);

        Queue<AutogradTensor> queue = new LinkedList<>();
        queue.add(this);

        while (!queue.isEmpty()) {
            AutogradTensor tensor = queue.poll();

            if (tensor.op != null) {
                AutogradTensor left = tensor.op.left;
                AutogradTensor right = tensor.op.right;

                switch (tensor.op.type) {
                    case "add":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            int[] indices = tensor.getIndices(i);
                            int[] leftIndices = left.getBroadcastIndices(i, tensor.shape);
                            int[] rightIndices = right.getBroadcastIndices(i, tensor.shape);
                            left.gradient[left.getFlatIndex(leftIndices)] += tensor.gradient[i];
                            right.gradient[right.getFlatIndex(rightIndices)] += tensor.gradient[i];
                        }
                        break;

                    case "subtract":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            int[] indices = tensor.getIndices(i);
                            int[] leftIndices = left.getBroadcastIndices(i, tensor.shape);
                            int[] rightIndices = right.getBroadcastIndices(i, tensor.shape);
                            left.gradient[left.getFlatIndex(leftIndices)] += tensor.gradient[i];
                            right.gradient[right.getFlatIndex(rightIndices)] -= tensor.gradient[i];
                        }
                        break;

                    case "multiply":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            int[] indices = tensor.getIndices(i);
                            int[] leftIndices = left.getBroadcastIndices(i, tensor.shape);
                            int[] rightIndices = right.getBroadcastIndices(i, tensor.shape);
                            left.gradient[left.getFlatIndex(leftIndices)] += tensor.gradient[i] * right.getElement(rightIndices);
                            right.gradient[right.getFlatIndex(rightIndices)] += tensor.gradient[i] * left.getElement(leftIndices);
                        }
                        break;

                    case "divide":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            int[] indices = tensor.getIndices(i);
                            int[] leftIndices = left.getBroadcastIndices(i, tensor.shape);
                            int[] rightIndices = right.getBroadcastIndices(i, tensor.shape);
                            double rightValue = right.getElement(rightIndices);
                            left.gradient[left.getFlatIndex(leftIndices)] += tensor.gradient[i] / rightValue;
                            right.gradient[right.getFlatIndex(rightIndices)] -= tensor.gradient[i] * left.getElement(leftIndices) / (rightValue * rightValue);
                        }
                        break;

                    case "exp":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            left.gradient[i] += tensor.gradient[i] * Math.exp(left.data[i]);
                        }
                        break;

                    case "log":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            left.gradient[i] += tensor.gradient[i] / left.data[i];
                        }
                        break;

                    case "pow":
                        double exponent = right.data[0];
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            left.gradient[i] += tensor.gradient[i] * exponent *
                                    Math.pow(left.data[i], exponent - 1);
                        }
                        break;

                    case "mean":
                        double gradientValue = tensor.gradient[0] / left.data.length;
                        for (int i = 0; i < left.gradient.length; i++) {
                            left.gradient[i] += gradientValue;
                        }
                        break;

                    case "sum":
                        double gradSum = tensor.gradient[0];
                        for (int i = 0; i < left.data.length; i++) {
                            left.gradient[i] += gradSum;
                        }
                        queue.add(left);
                        break;

                    case "relu":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            left.gradient[i] += (left.data[i] > 0) ? tensor.gradient[i] : 0;
                        }
                        break;

                    case "sigmoid":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            double sig = 1.0 / (1.0 + Math.exp(-left.data[i]));
                            left.gradient[i] += tensor.gradient[i] * sig * (1 - sig);
                        }
                        break;

                    case "tanh":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            double tanh_val = tensor.data[i];
                            left.gradient[i] += tensor.gradient[i] * (1 - tanh_val * tanh_val);
                        }
                        break;

                    default:
                        throw new UnsupportedOperationException("Unsupported operation: " + tensor.op.type);
                }

                if (left.op != null) queue.add(left);
                if (right != null && right.op != null) queue.add(right);
            }
        }
    }

    static class Operation {
        AutogradTensor output, left, right;
        String type;

        public Operation(AutogradTensor output, AutogradTensor left, AutogradTensor right, String type) {
            this.output = output;
            this.left = left;
            this.right = right;
            this.type = type;
        }

        Tensor getLeft() {
            return left;
        }

        public AutogradTensor getRight() {
            return right;
        }

        public String getType() {
            return type;
        }
    }

    public AutogradTensor relu() {
        AutogradTensor result = new AutogradTensor(this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.max(0, this.data[i]);
        }
        result.op = new Operation(result, this, null, "relu");
        return result;
    }

    public AutogradTensor sigmoid() {
        AutogradTensor result = new AutogradTensor(this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = 1.0 / (1.0 + Math.exp(-this.data[i]));
        }
        result.op = new Operation(result, this, null, "sigmoid");
        return result;
    }

    public AutogradTensor tanh() {
        AutogradTensor result = new AutogradTensor(this.shape);
        for (int i = 0; i < this.data.length; i++) {
            double e_pos = Math.exp(this.data[i]);
            double e_neg = Math.exp(-this.data[i]);
            result.data[i] = (e_pos - e_neg) / (e_pos + e_neg);
        }
        result.op = new Operation(result, this, null, "tanh");
        return result;
    }

}
