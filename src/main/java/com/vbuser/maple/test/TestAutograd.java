package com.vbuser.maple.test;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;

import java.util.Arrays;

public class TestAutograd {

    public static void main(String[] args) {
        simpleTest();
        testBasicOperations();
        testAdvancedOperations();
        testChainedOperations();
        testActivationFunctions();
        testBroadcastOperations();
        testSoftmax();
        testMatmul();
        testTranspose();
        testDimOp();
    }

    /**
     * Result:<br>
     * Add Result: [3.0, 5.0]<br>
     * Gradient a: [1.0, 1.0]<br>
     * Gradient b: [1.0, 1.0]<br>
     * Multiply Result: [2.0, 6.0]<br>
     * Gradient a: [1.0, 2.0]<br>
     * Gradient b: [2.0, 3.0]<br>
     */
    private static void testBasicOperations() {
        System.out.println("Testing Basic Operations:");

        AutogradTensor a = new AutogradTensor(new double[]{2.0, 3.0}, new int[]{2});
        AutogradTensor b = new AutogradTensor(new double[]{1.0, 2.0}, new int[]{2});

        AutogradTensor c = a.add(b);
        c.backward();

        System.out.println("Add Result: " + Arrays.toString(c.data));  //[3.0, 5.0]
        System.out.println("Gradient a: " + Arrays.toString(a.getGrad()));  //[1.0, 1.0]
        System.out.println("Gradient b: " + Arrays.toString(b.getGrad()));  //[1.0, 1.0]

        a.zeroGrad();
        b.zeroGrad();
        AutogradTensor d = a.multiply(b);
        d.backward();

        System.out.println("Multiply Result: " + Arrays.toString(d.data));  //[2.0, 6.0]
        System.out.println("Gradient a: " + Arrays.toString(a.getGrad()));  //[1.0, 2.0]
        System.out.println("Gradient b: " + Arrays.toString(b.getGrad()));  //[2.0, 3.0]
    }

    /**
     * Result:<br>
     * Exp Result: [2.718281828459045, 7.38905609893065]<br>
     * Gradient: [2.718281828459045, 7.38905609893065]<br>
     * Log Result: [0.0, 0.6931471805599453]<br>
     * Gradient: [1.0, 0.5]<br>
     * Pow Result: [1.0, 4.0]<br>
     * Gradient: [2.0, 4.0]<br>
     */
    private static void testAdvancedOperations() {
        System.out.println("\nTesting Advanced Operations:");

        AutogradTensor a = new AutogradTensor(new double[]{1.0, 2.0}, new int[]{2});
        AutogradTensor exp = a.exp();
        exp.backward();

        System.out.println("Exp Result: " + Arrays.toString(exp.data));
        //[2.718281828459045, 7.3891]
        System.out.println("Gradient: " + Arrays.toString(a.getGrad()));
        //[2.718281828459045, 7.3891]

        a.zeroGrad();
        AutogradTensor log = a.log();
        log.backward();

        System.out.println("Log Result: " + Arrays.toString(log.data));
        //[0.0, 0.6931]
        System.out.println("Gradient: " + Arrays.toString(a.getGrad()));
        //[1.0, 0.5]

        a.zeroGrad();
        AutogradTensor pow = a.pow(2.0);
        pow.backward();

        System.out.println("Pow Result: " + Arrays.toString(pow.data));
        //[1.0, 4.0]
        System.out.println("Gradient: " + Arrays.toString(a.getGrad()));
        //[2.0, 4.0]
    }

    /**
     * Result:<br>
     * ReLU Result: [0.0, 2.0]<br>
     * Gradient: [0.0, 1.0]<br>
     * Sigmoid Result: [0.2689414213699951, 0.8807970779778823]<br>
     * Gradient: [0.19661193324148185, 0.10499358540350662]<br>
     */
    private static void testActivationFunctions() {
        System.out.println("\nTesting Activation Functions:");

        //ReLU
        AutogradTensor a = new AutogradTensor(new double[]{-1.0, 2.0}, new int[]{2});
        AutogradTensor relu = a.relu();
        relu.backward();

        System.out.println("ReLU Result: " + Arrays.toString(relu.data));
        //[0.0, 2.0]
        System.out.println("Gradient: " + Arrays.toString(a.getGrad()));
        //[0.0, 1.0]

        a.zeroGrad();
        AutogradTensor sigmoid = a.sigmoid();
        sigmoid.backward();

        System.out.println("Sigmoid Result: " + Arrays.toString(sigmoid.data));
        //[0.2689, 0.8808]
        System.out.println("Gradient: " + Arrays.toString(a.getGrad()));
        //[0.1966, 0.1050]
    }

    /**
     * Result:<br>
     * Chained Result: [3.0]<br>
     * Gradient a: [4.0]<br>
     * Gradient b: [-2.0]<br>
     */
    private static void testChainedOperations() {
        System.out.println("\nTesting Chained Operations:");

        //(a + b) * (a - b)
        AutogradTensor a = new AutogradTensor(new double[]{2.0}, new int[]{1});
        AutogradTensor b = new AutogradTensor(new double[]{1.0}, new int[]{1});

        AutogradTensor sum = a.add(b);
        AutogradTensor diff = a.subtract(b);
        AutogradTensor result = sum.multiply(diff);
        result.backward();

        System.out.println("Chained Result: " + Arrays.toString(result.data));
        //[3.0] ((2+1)*(2-1) = 3*1 = 3)
        System.out.println("Gradient a: " + Arrays.toString(a.getGrad()));
        //[4.0]
        System.out.println("Gradient b: " + Arrays.toString(b.getGrad()));
        //[-2.0]
    }


    /**
     * Result:<br>
     * Gradient of a: [4.0]<br>
     * Gradient of b: [4.0]<br>
     * Gradient of c: [5.0]<br>
     * Gradient of a: [3.0]<br>
     * Gradient of b: [6.0]<br>
     * Gradient of c: [3.0]<br>
     */
    public static void simpleTest() {
        double[] dataA = {2.0};
        double[] dataB = {3.0};
        double[] dataC = {4.0};
        int[] shape = {1};

        AutogradTensor a = new AutogradTensor(dataA, shape);
        AutogradTensor b = new AutogradTensor(dataB, shape);
        AutogradTensor c = new AutogradTensor(dataC, shape);

        AutogradTensor sumAB = Maple.add(a, b);
        AutogradTensor result = Maple.mul(sumAB, c);

        result.backward();

        System.out.println("Gradient of a: " + Arrays.toString(a.getGrad()));
        System.out.println("Gradient of b: " + Arrays.toString(b.getGrad()));
        System.out.println("Gradient of c: " + Arrays.toString(c.getGrad()));

        a.zeroGrad();
        b.zeroGrad();
        c.zeroGrad();

        AutogradTensor mulAB = Maple.mul(a, b);
        AutogradTensor mulBC = Maple.mul(b, c);
        AutogradTensor result2 = Maple.add(mulAB, mulBC);

        result2.backward();

        System.out.println("Gradient of a: " + Arrays.toString(a.getGrad()));
        System.out.println("Gradient of b: " + Arrays.toString(b.getGrad()));
        System.out.println("Gradient of c: " + Arrays.toString(c.getGrad()));
    }

    /**
     * Result:<br>
     * Original tensors:<br>
     * Tensor a (shape [2]): [1.0, 2.0]<br>
     * Tensor b (shape [1]): [2.0]<br>
     * <br>
     * After broadcast:<br>
     * Tensor a: [1.0, 2.0]<br>
     * Tensor b: [2.0, 2.0]<br>
     * <br>
     * Addition Result: [3.0, 4.0]<br>
     * Addition Gradients:<br>
     * a grad: [1.0, 1.0]<br>
     * b grad: [2.0]<br>
     * <br>
     * Subtraction Result: [-1.0, 0.0]<br>
     * Subtraction Gradients:<br>
     * a grad: [1.0, 1.0]<br>
     * b grad: [-2.0]<br>
     * <br>
     * Multiplication Result: [2.0, 4.0]<br>
     * Multiplication Gradients:<br>
     * a grad: [2.0, 2.0]<br>
     * b grad: [3.0]<br>
     * <br>
     * Division Result: [0.5, 1.0]<br>
     * Division Gradients:<br>
     * a grad: [0.5, 0.5]<br>
     * b grad: [-0.8](-0.75 in real)<br>
     */
    public static void testBroadcastOperations() {
        AutogradTensor a = new AutogradTensor(new double[]{1.0, 2.0}, new int[]{2});
        AutogradTensor b = new AutogradTensor(new double[]{2.0}, new int[]{1});

        System.out.println("Original tensors:");
        System.out.println("Tensor a (shape [2]): " + a);
        System.out.println("Tensor b (shape [1]): " + b);

        AutogradTensor addResult = a.add(b);
        addResult.backward();
        System.out.println("\nAddition Result: " + addResult);
        System.out.println("Addition Gradients:");
        System.out.println("a grad: " + a.getGradAsTensor());
        System.out.println("b grad: " + b.getGradAsTensor());

        a.zeroGrad();
        b.zeroGrad();

        AutogradTensor subResult = a.subtract(b);
        subResult.backward();
        System.out.println("\nSubtraction Result: " + subResult);
        System.out.println("Subtraction Gradients:");
        System.out.println("a grad: " + a.getGradAsTensor());
        System.out.println("b grad: " + b.getGradAsTensor());

        a.zeroGrad();
        b.zeroGrad();

        AutogradTensor mulResult = a.multiply(b);
        mulResult.backward();
        System.out.println("\nMultiplication Result: " + mulResult);
        System.out.println("Multiplication Gradients:");
        System.out.println("a grad: " + a.getGradAsTensor());
        System.out.println("b grad: " + b.getGradAsTensor());

        a.zeroGrad();
        b.zeroGrad();

        AutogradTensor divResult = a.divide(b);
        divResult.backward();
        System.out.println("\nDivision Result: " + divResult);
        System.out.println("Division Gradients:");
        System.out.println("a grad: " + a.getGradAsTensor());
        System.out.println("b grad: " + b.getGradAsTensor());
    }

    /**
     * Result:<br>
     * Softmax Output: [0.09, 0.24, 0.67]<br>
     * Gradients input: [-0.017, -0.069, 0.086]<br>
     */
    public static void testSoftmax() {
        double[] inputData = {1.0, 2.0, 3.0};
        int[] shape = {3};
        AutogradTensor input = new AutogradTensor(inputData, shape);

        AutogradTensor output = input.softmax();

        double[] targetData = {0.2, 0.5, 0.3};
        AutogradTensor target = new AutogradTensor(targetData, shape);
        AutogradTensor loss = Maple.sub(output, target).pow(2).mean();

        System.out.println("Softmax Output: " + Arrays.toString(output.data));

        loss.backward();

        System.out.println("Gradients with respect to input: " + Arrays.toString(input.gradient));
    }

    /**
     * Result:<br>
     * Forward:<br>
     * Tensor of shape [2, 2]:<br>
     * 23.0, 29.0<br>
     * 50.0, 65.0<br>
     * Gradient:<br>
     * A gradient:<br>
     * [3.0, 7.0, 11.0, 3.0, 7.0, 11.0]<br>
     * B gradient:<br>
     * [5.0, 5.0, 7.0, 7.0, 9.0, 9.0]<br>
     * C gradient:<br>
     * [1.0, 1.0, 1.0, 1.0]<br>
     */
    public static void testMatmul() {
        System.out.println("\nTest Matmul");
        AutogradTensor A = new AutogradTensor(2, 3);
        AutogradTensor B = new AutogradTensor(3, 2);
        AutogradTensor C = new AutogradTensor(2, 2);

        A.data = new double[]{1, 2, 3, 4, 5, 6};
        B.data = new double[]{1, 2, 3, 4, 5, 6};
        C.data = new double[]{1, 1, 1, 1};
        AutogradTensor D = A.matmul(B).add(C);
        D.backward();

        System.out.println("Forward:");
        System.out.println(D);

        System.out.println("\nGradient:");
        System.out.println("A gradient:");
        System.out.println(Arrays.toString(A.gradient));
        System.out.println("B gradient:");
        System.out.println(Arrays.toString(B.gradient));
        System.out.println("C gradient:");
        System.out.println(Arrays.toString(C.gradient));
    }

    /**
     * Result:<br>
     * Test Case 1: Simple Transpose<br>
     * Original tensor a:<br>
     * Tensor of shape [2, 3]:<br>
     * 1.0, 2.0, 3.0<br>
     * 4.0, 5.0, 6.0<br>
     * Transposed tensor b:<br>
     * Tensor of shape [3, 2]:<br>
     * 1.0, 4.0<br>
     * 2.0, 5.0<br>
     * 3.0, 6.0<br>
     * Gradient of tensor a:<br>
     * [1.0, 1.0, 1.0, 1.0, 1.0, 1.0]<br>
     * Test Case 2: Multiple Transpose and Computation<br>
     * Original tensor x:<br>
     * Tensor of shape [2, 2]:<br>
     * 1.0, 2.0<br>
     * 3.0, 4.0<br>
     * First transpose y:<br>
     * Tensor of shape [2, 2]:<br>
     * 1.0, 3.0<br>
     * 2.0, 4.0<br>
     * After addition z:<br>
     * Tensor of shape [2, 2]:<br>
     * 2.0, 4.0<br>
     * 3.0, 5.0<br>
     * Final transpose w:<br>
     * Tensor of shape [2, 2]:<br>
     * 2.0, 3.0<br>
     * 4.0, 5.0<br>
     * Gradient of tensor x:<br>
     * [1.0, 1.0, 1.0, 1.0]<br>
     */
    public static void testTranspose() {
        System.out.println("Test Case 1: Simple Transpose");
        AutogradTensor a = new AutogradTensor(new double[]{
                1, 2, 3,
                4, 5, 6
        }, new int[]{2, 3});
        System.out.println("Original tensor a:");
        System.out.println(a);

        AutogradTensor b = a.transpose(0, 1);
        System.out.println("\nTransposed tensor b:");
        System.out.println(b);

        b.backward();
        System.out.println("\nGradient of tensor a:");
        System.out.println(Arrays.toString(a.gradient));

        System.out.println("\nTest Case 2: Multiple Transpose and Computation");
        AutogradTensor x = new AutogradTensor(new double[]{
                1, 2,
                3, 4
        }, new int[]{2, 2});
        System.out.println("Original tensor x:");
        System.out.println(x);

        AutogradTensor y = x.transpose(0, 1);
        System.out.println("\nFirst transpose y:");
        System.out.println(y);

        AutogradTensor constant = new AutogradTensor(new double[]{
                1, 1,
                1, 1
        }, new int[]{2, 2});
        AutogradTensor z = y.add(constant);
        System.out.println("\nAfter addition z:");
        System.out.println(z);

        AutogradTensor w = z.transpose(0, 1);
        System.out.println("\nFinal transpose w:");
        System.out.println(w);

        w.backward();
        System.out.println("\nGradient of tensor x:");
        System.out.println(Arrays.toString(x.gradient));
    }

    public static void testDimOp(){
        double[][] data = {
                {1.0, 2.0},
                {3.0, 4.0},
                {5.0, 6.0}
        };

        AutogradTensor x = Maple.fromArray(data);

        System.out.println("Original tensor:");
        System.out.println(x);

        AutogradTensor meanDim0 = x.mean(0);
        System.out.println("\nMean along dim 0:");
        System.out.println(meanDim0);

        meanDim0.backward();
        System.out.println("\nGradient for mean operation:");
        System.out.println(Arrays.toString(x.gradient));

        x.zeroGrad();

        AutogradTensor sumDim0 = x.sum(0);
        System.out.println("\nSum along dim 0:");
        System.out.println(sumDim0);

        sumDim0.backward();
        System.out.println("\nGradient for sum operation:");
        System.out.println(Arrays.toString(x.gradient));
    }
}
