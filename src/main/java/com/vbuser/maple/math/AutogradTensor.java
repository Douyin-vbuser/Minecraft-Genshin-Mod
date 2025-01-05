package com.vbuser.maple.math;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

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

    public static AutogradTensor randn(int... shape) {
        AutogradTensor tensor = new AutogradTensor(shape);
        Random random = new Random();
        for (int i = 0; i < tensor.data.length; i++) {
            tensor.data[i] = random.nextGaussian();
        }
        return tensor;
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

    private void sumOverDimensions(int[] currentIndex, int[] dims, int dimIdx,
                                   double[] resultData, int[] resultShape) {
        if (dimIdx == this.shape.length) {
            int sourceIndex = flattenIndex(currentIndex, this.shape);
            int[] targetIndex = new int[resultShape.length];
            int targetIdx = 0;
            for (int i = 0; i < this.shape.length; i++) {
                if (Arrays.binarySearch(dims, i) < 0) {
                    targetIndex[targetIdx++] = currentIndex[i];
                }
            }
            resultData[flattenIndex(targetIndex, resultShape)] += this.data[sourceIndex];
            return;
        }

        for (int i = 0; i < this.shape[dimIdx]; i++) {
            currentIndex[dimIdx] = i;
            sumOverDimensions(currentIndex, dims, dimIdx + 1, resultData, resultShape);
        }
    }

    public AutogradTensor sum(int... dims) {
        if (dims == null || dims.length == 0) {
            double sum = 0;
            for (double value : this.data) {
                sum += value;
            }
            AutogradTensor result = new AutogradTensor(new double[]{sum}, new int[]{1});
            result.op = new Operation(result, this, null, "sum");
            return result;
        }

        Arrays.sort(dims);
        int[] newShape = new int[this.shape.length - dims.length];
        int idx = 0;
        for (int i = 0; i < this.shape.length; i++) {
            if (Arrays.binarySearch(dims, i) < 0) {
                newShape[idx++] = this.shape[i];
            }
        }

        double[] resultData = new double[Arrays.stream(newShape).reduce(1, (a, b) -> a * b)];
        int[] currentIndex = new int[this.shape.length];

        sumOverDimensions(currentIndex, dims, 0, resultData, newShape);

        AutogradTensor result = new AutogradTensor(resultData, newShape);
        result.op = new Operation(result, this, null, "sum_dim");
        result.op.dims = dims;
        return result;
    }

    private int flattenIndex(int[] indices, int[] shape) {
        int index = 0;
        int stride = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            index += indices[i] * stride;
            stride *= shape[i];
        }
        return index;
    }

    public AutogradTensor mean(int... dims) {
        AutogradTensor sumResult = sum(dims);
        int elementsToAverage = 1;
        if (dims == null || dims.length == 0) {
            elementsToAverage = this.data.length;
        } else {
            for (int dim : dims) {
                elementsToAverage *= this.shape[dim];
            }
        }

        double[] meanData = new double[sumResult.data.length];
        for (int i = 0; i < sumResult.data.length; i++) {
            meanData[i] = sumResult.data[i] / elementsToAverage;
        }

        AutogradTensor result = new AutogradTensor(meanData, sumResult.shape);
        result.op = new Operation(result, this, null, "mean_dim");
        result.op.dims = dims;
        return result;
    }

    public AutogradTensor matmul(AutogradTensor other) {
        if (this.shape[1] != other.shape[0]) {
            throw new IllegalArgumentException("Incompatible dimensions for matrix multiplication");
        }

        int[] resultShape = {this.shape[0], other.shape[1]};
        AutogradTensor result = new AutogradTensor(resultShape);

        for (int i = 0; i < this.shape[0]; i++) {
            for (int j = 0; j < other.shape[1]; j++) {
                double sum = 0;
                for (int k = 0; k < this.shape[1]; k++) {
                    sum += this.getElement(i, k) * other.getElement(k, j);
                }
                result.data[i * other.shape[1] + j] = sum;
            }
        }

        result.op = new Operation(result, this, other, "matmul");
        return result;
    }

    public AutogradTensor transpose(int dim1, int dim2) {
        if (dim1 < 0 || dim1 >= shape.length || dim2 < 0 || dim2 >= shape.length) {
            throw new IllegalArgumentException("Invalid dimensions for transpose");
        }

        int[] newShape = shape.clone();
        newShape[dim1] = shape[dim2];
        newShape[dim2] = shape[dim1];

        AutogradTensor result = new AutogradTensor(newShape);

        for (int i = 0; i < data.length; i++) {
            int[] indices = getIndices(i, shape);
            int temp = indices[dim1];
            indices[dim1] = indices[dim2];
            indices[dim2] = temp;
            result.data[getFlatIndex(indices, newShape)] = data[i];
        }

        result.op = new Operation(result, this, null, "transpose");
        result.op.dim1 = dim1;
        result.op.dim2 = dim2;

        return result;
    }

    public AutogradTensor reshape(int... newShape) {
        int newSize = 1;
        for (int dim : newShape) {
            newSize *= dim;
        }
        int originalSize = 1;
        for (int dim : this.shape) {
            originalSize *= dim;
        }
        if (newSize != originalSize) {
            throw new IllegalArgumentException("New shape must have same number of elements as original shape");
        }
        AutogradTensor result = new AutogradTensor(newShape);
        System.arraycopy(this.data, 0, result.data, 0, this.data.length);
        result.op = new Operation(result, this, null, "reshape");
        result.op.originalShape = this.shape;
        return result;
    }

    public int getFlatIndex(int[] indices, int[] shape) {
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

                    case "matmul":
                        AutogradTensor gradientTensor = new AutogradTensor(tensor.shape);
                        System.arraycopy(tensor.gradient, 0, gradientTensor.data, 0, tensor.gradient.length);

                        for (int i = 0; i < left.shape[0]; i++) {
                            for (int k = 0; k < left.shape[1]; k++) {
                                double sum = 0;
                                for (int j = 0; j < right.shape[1]; j++) {
                                    sum += gradientTensor.getElement(i, j) *
                                            right.getElement(k, j);
                                }
                                left.gradient[i * left.shape[1] + k] += sum;
                            }
                        }

                        for (int k = 0; k < right.shape[0]; k++) {
                            for (int j = 0; j < right.shape[1]; j++) {
                                double sum = 0;
                                for (int i = 0; i < left.shape[0]; i++) {
                                    sum += left.getElement(i, k) *
                                            gradientTensor.getElement(i, j);
                                }
                                right.gradient[k * right.shape[1] + j] += sum;
                            }
                        }
                        break;

                    case "transpose":
                        AutogradTensor input = tensor.op.left;
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            int[] indices = getIndices(i, tensor.shape);
                            int temp = indices[tensor.op.dim1];
                            indices[tensor.op.dim1] = indices[tensor.op.dim2];
                            indices[tensor.op.dim2] = temp;
                            input.gradient[input.getFlatIndex(indices)] += tensor.gradient[i];
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

                    case "softmax":
                        for (int i = 0; i < tensor.data.length; i++) {
                            for (int j = 0; j < tensor.data.length; j++) {
                                double gradientContribution;
                                if (i == j) {
                                    gradientContribution = tensor.data[i] * (1 - tensor.data[i]);
                                } else {
                                    gradientContribution = -tensor.data[i] * tensor.data[j];
                                }
                                left.gradient[i] += tensor.gradient[j] * gradientContribution;
                            }
                        }
                        break;

                    case "reshape":
                        for (int i = 0; i < tensor.gradient.length; i++) {
                            tensor.op.left.gradient[i] += tensor.gradient[i];
                        }
                        break;

                    case "sum_dim":
                        for (int i = 0; i < left.data.length; i++) {
                            int[] originalIndices = left.getIndices(i);
                            int[] resultIndices = getReducedIndices(originalIndices, tensor.op.dims, tensor.shape);
                            left.gradient[i] += tensor.gradient[tensor.getFlatIndex(resultIndices)];
                        }
                        break;

                    case "mean_dim":
                        int elementsCount = 1;
                        for (int dim : tensor.op.dims) {
                            elementsCount *= left.shape[dim];
                        }

                        for (int i = 0; i < left.data.length; i++) {
                            int[] originalIndices = left.getIndices(i);
                            int[] resultIndices = getReducedIndices(originalIndices, tensor.op.dims, tensor.shape);
                            left.gradient[i] += tensor.gradient[tensor.getFlatIndex(resultIndices)] / elementsCount;
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

    private static int[] getReducedIndices(int[] originalIndices, int[] dims, int[] resultShape) {
        int[] resultIndices = new int[resultShape.length];
        int resultIdx = 0;

        for (int i = 0; i < originalIndices.length; i++) {
            boolean isDimToReduce = false;
            for (int dim : dims) {
                if (i == dim) {
                    isDimToReduce = true;
                    break;
                }
            }
            if (!isDimToReduce) {
                resultIndices[resultIdx++] = originalIndices[i];
            }
        }

        return resultIndices;
    }

    static class Operation {
        AutogradTensor output, left, right;
        String type;
        public int dim1, dim2;
        public int[] originalShape;
        public int[] dims;

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

    public AutogradTensor softmax() {
        AutogradTensor result = new AutogradTensor(this.shape);
        double[] expData = new double[this.data.length];
        double sumExp = 0.0;
        for (int i = 0; i < this.data.length; i++) {
            expData[i] = Math.exp(this.data[i]);
            sumExp += expData[i];
        }
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = expData[i] / sumExp;
        }
        result.op = new Operation(result, this, null, "softmax");
        return result;
    }

}
