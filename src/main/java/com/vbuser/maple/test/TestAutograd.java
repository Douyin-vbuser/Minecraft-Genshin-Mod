package com.vbuser.maple.test;

import com.vbuser.maple.Maple;
import com.vbuser.maple.math.AutogradTensor;
import com.vbuser.maple.math.F;

public class TestAutograd {

    public static void main(String[] args){
        testAddition();
        testMean();
        testMultiplication();
        testPower();
        testSubtraction();
        testSum();
        testReLU();
        testBroadcast();
    }
    public static void testAddition() {
        AutogradTensor a = new AutogradTensor(new double[]{1, 2, 3}, new int[]{3});
        AutogradTensor b = new AutogradTensor(new double[]{4, 5, 6}, new int[]{3});
        AutogradTensor result = Maple.add(a, b);
        System.out.println("Addition Result: " + result);
        //assertArrayEquals(new double[]{5, 7, 9}, result.data, 1e-6);
    }

    public static void testSubtraction() {
        AutogradTensor a = new AutogradTensor(new double[]{5, 7, 9}, new int[]{3});
        AutogradTensor b = new AutogradTensor(new double[]{1, 2, 3}, new int[]{3});
        AutogradTensor result = Maple.sub(a, b);
        System.out.println("Subtraction Result: " + result);
        //assertArrayEquals(new double[]{4, 5, 6}, result.data, 1e-6);
    }

    public static void testMultiplication() {
        AutogradTensor a = new AutogradTensor(new double[]{1, 2, 3}, new int[]{3});
        AutogradTensor b = new AutogradTensor(new double[]{2, 3, 4}, new int[]{3});
        AutogradTensor result = Maple.mul(a, b);
        System.out.println("Multiplication Result: " + result);
        //assertArrayEquals(new double[]{2, 6, 12}, result.data, 1e-6);
    }

    public static void testPower() {
        AutogradTensor a = new AutogradTensor(new double[]{1, 2, 3}, new int[]{3});
        AutogradTensor result = Maple.pow(a, 2);
        System.out.println("Power Result: " + result);
        //assertArrayEquals(new double[]{1, 4, 9}, result.data, 1e-6);
    }

    public static void testSum() {
        AutogradTensor a = new AutogradTensor(new double[]{1, 2, 3, 4}, new int[]{2, 2});
        AutogradTensor result = Maple.sum(a);
        System.out.println("Sum Result: " + result);
        //assertEquals(10.0, result.data[0], 1e-6);
    }

    public static void testMean() {
        AutogradTensor a = new AutogradTensor(new double[]{1, 2, 3, 4}, new int[]{2, 2});
        AutogradTensor result = Maple.mean(a);
        System.out.println("Mean Result: " + result);
        //assertEquals(2.5, result.data[0], 1e-6);
    }

    public static void testReLU() {
        AutogradTensor a = new AutogradTensor(new double[]{-1, 0, 1, 2}, new int[]{4});
        AutogradTensor result = F.relu(a);
        System.out.println("ReLU Result: " + result);
        //assertArrayEquals(new double[]{0, 0, 1, 2}, result.data, 1e-6);
    }

    public static void testBroadcast() {
        AutogradTensor a = new AutogradTensor(new double[]{1,2},new int[]{2,1});
        AutogradTensor b = new AutogradTensor(new double[]{3, 4, 5},new int[]{3});
        AutogradTensor[] result = AutogradTensor.broadcast(a, b);

        System.out.println("ReLU Result:");
        System.out.println(result[0]+"\n"+result[1]);

        System.out.println(a.add(b));

    }

}
