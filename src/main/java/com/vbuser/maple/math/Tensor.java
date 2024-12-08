package com.vbuser.maple.math;

import java.util.Arrays;
import java.util.Random;

@SuppressWarnings("unused")
public class Tensor {

    public double[] data;
    public int[] shape;

    public Tensor(double[] data, int[] shape) {
        this.data = data;
        this.shape = shape;
    }

    public static Tensor ones(int... shape) {
        Tensor tensor = new Tensor(shape);
        Arrays.fill(tensor.data, 1.0);
        return tensor;
    }

    public static Tensor zeros(int... shape) {
        return new Tensor(shape);
    }

    public static Tensor randn(int... shape) {
        Tensor tensor = new Tensor(shape);
        Random random = new Random();
        for (int i = 0; i < tensor.data.length; i++) {
            tensor.data[i] = random.nextGaussian();
        }
        return tensor;
    }

    public Tensor(int... shape) {
        this.shape = shape;
        int size = 1;
        for (int dim : shape) {
            size *= dim;
        }
        this.data = new double[size];
    }

    public Tensor(double[] data) {
        this.data = Arrays.copyOf(data, data.length);
        this.shape = new int[]{data.length};
    }

    public Tensor(double[][] data) {
        if (data == null || data.length == 0 || data[0].length == 0) {
            throw new IllegalArgumentException("Input data cannot be null or empty");
        }

        int rows = data.length;
        int cols = data[0].length;
        this.shape = new int[]{rows, cols};

        for (int i = 1; i < rows; i++) {
            if (data[i].length != cols) {
                throw new IllegalArgumentException("All rows must have the same number of elements");
            }
        }

        this.data = new double[rows * cols];
        for (int i = 0; i < rows; i++) {
            System.arraycopy(data[i], 0, this.data, i * cols, cols);
        }
    }

    public int[] getShape() {
        return shape;
    }

    public double getElement(int... indices) {
        if (indices.length != shape.length) {
            throw new IllegalArgumentException("Indices do not match tensor dimensions");
        }
        int index = computeIndex(indices);
        return data[index];
    }

    public void setElement(double value, int... indices) {
        if (indices.length != shape.length) {
            throw new IllegalArgumentException("Indices do not match tensor dimensions");
        }
        int index = computeIndex(indices);
        data[index] = value;
    }

    private int computeIndex(int[] indices) {
        int index = 0;
        int stride = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            if (indices[i] < 0 || indices[i] >= shape[i]) {
                throw new IndexOutOfBoundsException("Index out of bounds");
            }
            index += indices[i] * stride;
            stride *= shape[i];
        }
        return index;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Tensor of shape ").append(java.util.Arrays.toString(shape)).append(":\n");
        appendFormattedData(sb, new int[shape.length], 0);
        return sb.toString().trim();
    }

    private void appendFormattedData(StringBuilder sb, int[] indices, int dim) {
        if (dim == shape.length) {
            sb.append(String.format("%.1f", data[computeIndex(indices)]));
            return;
        }

        if (dim == shape.length - 1) {
            for (int i = 0; i < shape[dim]; i++) {
                indices[dim] = i;
                sb.append(String.format("%.1f", data[computeIndex(indices)]));
                if (i < shape[dim] - 1) {
                    sb.append(", ");
                }
            }
        } else {
            for (int i = 0; i < shape[dim]; i++) {
                indices[dim] = i;
                appendFormattedData(sb, indices, dim + 1);
                if (dim == 0 && i < shape[dim] - 1) {
                    sb.append("\n");
                } else if (dim > 0) {
                    sb.append("\n");
                }
            }
        }
    }

    public int[] size() {
        return java.util.Arrays.copyOf(shape, shape.length);
    }

    public Tensor reshape(int... newShape) {
        int newSize = 1;
        for (int dim : newShape) {
            newSize *= dim;
        }
        if (newSize != data.length) {
            throw new IllegalArgumentException("New shape does not match the number of elements");
        }
        Tensor reshapedTensor = new Tensor(newShape);
        System.arraycopy(data, 0, reshapedTensor.data, 0, data.length);
        return reshapedTensor;
    }

    public int[] shape() {
        return shape;
    }

    public Tensor add(Tensor other) {
        Tensor[] broadcasted = broadcast(this, other);
        Tensor a = broadcasted[0];
        Tensor b = broadcasted[1];

        Tensor result = new Tensor(a.shape);
        for (int i = 0; i < a.data.length; i++) {
            result.data[i] = a.data[i] + b.data[i];
        }
        return result;
    }

    public Tensor sub(Tensor other) {
        Tensor[] broadcasted = broadcast(this, other);
        Tensor a = broadcasted[0];
        Tensor b = broadcasted[1];

        Tensor result = new Tensor(a.shape);
        for (int i = 0; i < a.data.length; i++) {
            result.data[i] = a.data[i] - b.data[i];
        }
        return result;
    }

    public Tensor mul(Tensor other) {
        Tensor[] broadcasted = broadcast(this, other);
        Tensor a = broadcasted[0];
        Tensor b = broadcasted[1];

        Tensor result = new Tensor(a.shape);
        for (int i = 0; i < a.data.length; i++) {
            result.data[i] = a.data[i] * b.data[i];
        }
        return result;
    }

    public Tensor div(Tensor other) {
        Tensor[] broadcasted = broadcast(this, other);
        Tensor a = broadcasted[0];
        Tensor b = broadcasted[1];

        Tensor result = new Tensor(a.shape);
        for (int i = 0; i < a.data.length; i++) {
            if (b.data[i] == 0) {
                throw new ArithmeticException("Division by zero is not allowed");
            }
            result.data[i] = a.data[i] / b.data[i];
        }
        return result;
    }

    public Tensor pow(Tensor exponent) {
        Tensor[] broadcasted = broadcast(this, exponent);
        Tensor a = broadcasted[0];
        Tensor b = broadcasted[1];

        Tensor result = new Tensor(a.shape);
        for (int i = 0; i < a.data.length; i++) {
            result.data[i] = Math.pow(a.data[i], b.data[i]);
        }
        return result;
    }

    public Tensor pow(double exponent) {
        Tensor result = new Tensor(this.shape);
        for (int i = 0; i < this.data.length; i++) {
            result.data[i] = Math.pow(this.data[i], exponent);
        }
        return result;
    }

    public static Tensor exp(Tensor a) {
        Tensor result = new Tensor(a.shape);
        for (int i = 0; i < a.data.length; i++) {
            result.data[i] = Math.exp(a.data[i]);
        }
        return result;
    }

    public static Tensor cat(Tensor[] tensors, int dim) {
        int[] resultShape = getInts(tensors, dim);
        int concatSize = 0;
        for (Tensor t : tensors) {
            concatSize += t.shape[dim];
        }
        resultShape[dim] = concatSize;

        Tensor result = new Tensor(resultShape);
        int offset = 0;
        for (Tensor t : tensors) {
            int length = t.data.length;
            System.arraycopy(t.data, 0, result.data, offset, length);
            offset += length;
        }

        return result;
    }

    private static int[] getInts(Tensor[] tensors, int dim) {
        if (tensors == null || tensors.length < 2) {
            throw new IllegalArgumentException("At least two tensors are required for concatenation");
        }

        int[] firstShape = tensors[0].shape;
        for (int i = 1; i < tensors.length; i++) {
            if (!shapesMatchExceptDim(tensors[i].shape, firstShape, dim)) {
                throw new IllegalArgumentException("All tensor shapes must match except for the concatenation dimension");
            }
        }

        return Arrays.copyOf(firstShape, firstShape.length);
    }

    private static boolean shapesMatchExceptDim(int[] shapeA, int[] shapeB, int dim) {
        if (shapeA.length != shapeB.length) {
            return false;
        }
        for (int i = 0; i < shapeA.length; i++) {
            if (i != dim && shapeA[i] != shapeB[i]) {
                return false;
            }
        }
        return true;
    }

    public static Tensor equal(Tensor a, Tensor b) {
        if (!Arrays.equals(a.shape, b.shape)) {
            throw new IllegalArgumentException("Shapes must match for element-wise comparison");
        }

        Tensor result = new Tensor(a.shape);
        for (int i = 0; i < a.data.length; i++) {
            result.data[i] = a.data[i] == b.data[i] ? 1.0 : 0.0;
        }

        return result;
    }

    public static Tensor[] broadcast(Tensor a, Tensor b) {
        int[] shapeA = a.shape;
        int[] shapeB = b.shape;
        int maxDims = Math.max(shapeA.length, shapeB.length);
        int[] newShape = new int[maxDims];

        for (int i = 1; i <= maxDims; i++) {
            int dimA = i <= shapeA.length ? shapeA[shapeA.length - i] : 1;
            int dimB = i <= shapeB.length ? shapeB[shapeB.length - i] : 1;

            if (dimA == dimB) {
                newShape[maxDims - i] = dimA;
            } else if (dimA == 1) {
                newShape[maxDims - i] = dimB;
            } else if (dimB == 1) {
                newShape[maxDims - i] = dimA;
            } else {
                throw new IllegalArgumentException("Shapes cannot be broadcast together");
            }
        }

        Tensor broadcastA = broadcastTo(a, newShape);
        Tensor broadcastB = broadcastTo(b, newShape);

        return new Tensor[]{broadcastA, broadcastB};
    }

    private static Tensor broadcastTo(Tensor tensor, int[] newShape) {
        if (Arrays.equals(tensor.shape, newShape)) {
            return new Tensor(tensor.data.clone(), newShape);
        }

        int newSize = 1;
        for (int dim : newShape) {
            newSize *= dim;
        }

        double[] newData = new double[newSize];
        int[] oldStrides = calculateStrides(tensor.shape);
        int[] newStrides = calculateStrides(newShape);

        for (int i = 0; i < newSize; i++) {
            int oldIndex = 0;
            for (int j = 0; j < newShape.length; j++) {
                int dim = (i / newStrides[j]) % newShape[j];
                if (j >= newShape.length - tensor.shape.length) {
                    int oldDim = j - (newShape.length - tensor.shape.length);
                    if (tensor.shape[oldDim] > 1) {
                        oldIndex += (dim % tensor.shape[oldDim]) * oldStrides[oldDim];
                    }
                }
            }
            newData[i] = tensor.data[oldIndex];
        }

        return new Tensor(newData, newShape);
    }

    private static int[] calculateStrides(int[] shape) {
        int[] strides = new int[shape.length];
        int stride = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            strides[i] = stride;
            stride *= shape[i];
        }
        return strides;
    }

    public int[] getIndices(int flatIndex) {
        int[] indices = new int[shape.length];
        for (int i = shape.length - 1; i >= 0; i--) {
            indices[i] = flatIndex % shape[i];
            flatIndex /= shape[i];
        }
        return indices;
    }

    public int getIndex(int[] indices) {
        if (indices.length != shape.length) {
            throw new IllegalArgumentException("Indices dimension does not match tensor dimension");
        }
        int flatIndex = 0;
        int multiplier = 1;
        for (int i = shape.length - 1; i >= 0; i--) {
            flatIndex += indices[i] * multiplier;
            multiplier *= shape[i];
        }
        return flatIndex;
    }

    private int flattenIndex(int[] indices) {
        int index = 0;
        int multiplier = 1;
        for (int i = indices.length - 1; i >= 0; i--) {
            index += indices[i] * multiplier;
            multiplier *= shape[i];
        }
        return index;
    }

}
