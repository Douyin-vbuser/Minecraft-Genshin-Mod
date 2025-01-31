package com.vbuser.maple;

import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.math.Tensor;

import java.util.Arrays;

@SuppressWarnings("unused")
public class Maple {

    /**
     * Create New Tensor:
     */

    public static AutogradTensor zeros(int... shape) {
        return fromTensor(Tensor.zeros(shape));
    }

    public static AutogradTensor ones(int... shape) {
        return fromTensor(Tensor.ones(shape));
    }

    public static AutogradTensor randn(int... shape) {
        return fromTensor(Tensor.randn(shape));
    }

    public static AutogradTensor fromArray(double[][] array) {
        Tensor tensor = new Tensor(array);
        return new AutogradTensor(tensor.data, tensor.shape);
    }

    public static AutogradTensor fromNum(AutogradTensor tensor, double num) {
        AutogradTensor temp = new AutogradTensor(tensor.shape);
        Arrays.fill(temp.data, num);
        return temp;
    }

    public static AutogradTensor fromTensor(Tensor tensor) {
        return new AutogradTensor(tensor.data, tensor.shape);
    }

    /**
     * Calculation of autogradTensor:
     */

    public static AutogradTensor add(AutogradTensor a, AutogradTensor b) {
        return a.add(b);
    }

    public static AutogradTensor add(AutogradTensor a, double b) {
        return a.add(fromNum(a, b));
    }

    public static AutogradTensor sub(AutogradTensor a, AutogradTensor b) {
        return a.subtract(b);
    }

    public static AutogradTensor sub(AutogradTensor a, double b) {
        return a.subtract(fromNum(a, b));
    }

    public static AutogradTensor mul(AutogradTensor a, AutogradTensor b) {
        return a.multiply(b);
    }

    public static AutogradTensor mul(AutogradTensor a, double b) {
        return a.multiply(fromNum(a, b));
    }

    public static AutogradTensor div(AutogradTensor a, AutogradTensor b) {
        return a.divide(b);
    }

    public static AutogradTensor div(AutogradTensor a, double b) {
        return a.divide(fromNum(a, b));
    }

    public static AutogradTensor pow(AutogradTensor a, double b) {
        return a.pow(b);
    }

    public static AutogradTensor exp(AutogradTensor a) {
        return a.exp();
    }

    public static AutogradTensor log(AutogradTensor a) {
        return a.log();
    }

    public static AutogradTensor mean(AutogradTensor a) {
        return a.mean();
    }

    public static AutogradTensor mean(AutogradTensor a, int[] b) {
        return a.mean(b);
    }

    public static AutogradTensor sum(AutogradTensor a) {
        return a.sum();
    }

    public static AutogradTensor sum(AutogradTensor a, int[] b) {
        return a.mean(b);
    }

    public static AutogradTensor matmul(AutogradTensor a, AutogradTensor b) {
        return a.matmul(b);
    }

    public static AutogradTensor transpose(AutogradTensor a, int dim1, int dim2) {
        dim1 = dim1 < 0 ? dim1 + a.shape.length : dim1;
        dim2 = dim2 < 0 ? dim2 + a.shape.length : dim2;
        return a.transpose(dim1, dim2);
    }

    public static AutogradTensor softmax(AutogradTensor tensor, int axis) {
        axis = axis < 0 ? axis + tensor.shape.length : axis;
        return tensor.softmax(axis);
    }
}
