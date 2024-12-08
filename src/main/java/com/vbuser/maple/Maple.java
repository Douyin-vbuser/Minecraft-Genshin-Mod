package com.vbuser.maple;

import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.math.Tensor;

@SuppressWarnings("unused")
public class Maple {

    /**
     * Create New Tensor:
     */

    public static Tensor zeros(int... shape) {
        return Tensor.zeros(shape);
    }

    public static Tensor ones(int... shape) {
        return Tensor.ones(shape);
    }

    public static Tensor randn(int... shape) {
        return Tensor.randn(shape);
    }

    /**
     * Tensor calculations:
     */

    public static Tensor add(Tensor a, Tensor b) {
        return a.add(b);
    }

    public static Tensor sub(Tensor a, Tensor b) {
        return a.sub(b);
    }

    public static Tensor mul(Tensor a, Tensor b) {
        return a.mul(b);
    }

    public static Tensor div(Tensor a, Tensor b) {
        return a.div(b);
    }

    public static Tensor pow(Tensor a, double exponent) {
        return a.pow(exponent);
    }

    public static Tensor pow(Tensor a, Tensor exponent) {
        return a.pow(exponent);
    }

    public static Tensor exp(Tensor a) {
        return Tensor.exp(a);
    }

    public static Tensor cat(Tensor a, Tensor b, int dim) {
        Tensor[] temp = {a, b};
        return Tensor.cat(temp, dim);
    }

    public static Tensor equ(Tensor a, Tensor b) {
        return Tensor.equal(a, b);
    }

    /**
     * Calculation of autogradTensor:
     */

    public static AutogradTensor add(AutogradTensor a, AutogradTensor b) {
        return a.add(b);
    }

    public static AutogradTensor sub(AutogradTensor a,AutogradTensor b){
        return a.sub(b);
    }

    public static AutogradTensor mul(AutogradTensor a, AutogradTensor b) {
        return a.mul(b);
    }

    public static AutogradTensor pow(AutogradTensor a, double exponent) {
        return a.pow(exponent);
    }

    public static AutogradTensor sum(AutogradTensor a) {
        return AutogradTensor.sum(a);
    }

    public static AutogradTensor mean(AutogradTensor a){
        return a.mean();
    }


}
