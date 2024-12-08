package com.vbuser.maple.test;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.Tensor;

public class TestCalculation {
    public static void main(String[] args) {
        // Test tensor creation
        System.out.println("Creating tensors:");
        Tensor zeros = Maple.zeros(2, 3);
        System.out.println("Zeros: " + zeros);

        Tensor ones = Maple.ones(3, 2);
        System.out.println("Ones: " + ones);

        Tensor random = Maple.randn(2, 2);
        System.out.println("Random: " + random);

        // Test tensor calculations
        System.out.println("\nTensor calculations:");
        Tensor a = Maple.ones(2, 2);
        Tensor b = Maple.randn(2, 2);

        System.out.println("a: " + a);
        System.out.println("b: " + b);

        Tensor sum = Maple.add(a, b);
        System.out.println("a + b: " + sum);

        Tensor diff = Maple.sub(a, b);
        System.out.println("a - b: " + diff);

        Tensor product = Maple.mul(a, b);
        System.out.println("a * b: " + product);

        Tensor division = Maple.div(a, b);
        System.out.println("a / b: " + division);

        Tensor powered = Maple.pow(a, 2);
        System.out.println("a^2: " + powered);

        Tensor exponential = Maple.exp(a);
        System.out.println("exp(a): " + exponential);

        Tensor concatenated = Maple.cat(a, b, 0);
        System.out.println("cat(a, b, 0): " + concatenated);

        Tensor equal = Maple.equ(a, b);
        System.out.println("a == b: " + equal);
    }
}
