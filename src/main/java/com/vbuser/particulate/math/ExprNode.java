//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:DeepSeek
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.math;

import java.util.Map;

/**
 * ExprNode 接口定义了数学表达式节点的基本行为<br>
 * 所有表达式节点都必须实现评估方法，用于计算表达式的值
 */
public interface ExprNode {

    /**
     * 评估表达式节点的值
     * @param variables 变量映射表，包含表达式计算所需的所有变量值
     * @return 表达式的计算结果
     */
    double evaluate(Map<String, Double> variables);
}

/**
 * NumberNode 类表示数值常量节点<br>
 * 封装一个固定的数值，评估时直接返回该值
 */
class NumberNode implements ExprNode {
    private final double value;

    /**
     * 构造函数
     * @param value 数值常量的值
     */
    public NumberNode(double value) {
        this.value = value;
    }

    /**
     * 评估数值节点，直接返回存储的数值
     * @param variables 变量映射表（此节点不使用）
     * @return 存储的数值
     */
    @Override
    public double evaluate(Map<String, Double> variables) {
        return value;
    }
}

/**
 * VariableNode 类表示变量节点<br>
 * 封装一个变量名，评估时从变量映射表中获取对应的值
 */
class VariableNode implements ExprNode {
    private final String name;

    /**
     * 构造函数
     * @param name 变量名称
     */
    public VariableNode(String name) {
        this.name = name;
    }

    /**
     * 评估变量节点，从变量映射表中获取对应的值
     * @param variables 变量映射表，包含变量名和对应的值
     * @return 变量的值，如果变量不存在则返回0.0
     */
    @Override
    public double evaluate(Map<String, Double> variables) {
        return variables.getOrDefault(name, 0.0);
    }
}

/**
 * BinaryOpNode 类表示二元操作节点<br>
 * 封装一个二元操作（如加减乘除）和左右操作数
 */
class BinaryOpNode implements ExprNode {
    private final ExprNode left;
    private final ExprNode right;
    private final String operator;

    /**
     * 构造函数
     * @param left 左操作数表达式节点
     * @param operator 操作符字符串（如"+", "-", "*", "/", "^"）
     * @param right 右操作数表达式节点
     */
    public BinaryOpNode(ExprNode left, String operator, ExprNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    /**
     * 评估二元操作节点，根据操作符对左右操作数进行相应的计算
     * @param variables 变量映射表，包含表达式计算所需的所有变量值
     * @return 二元操作的计算结果
     * @throws ArithmeticException 当除零操作发生时抛出
     * @throws IllegalArgumentException 当操作符不被支持时抛出
     */
    @Override
    public double evaluate(Map<String, Double> variables) {
        double leftVal = left.evaluate(variables);
        double rightVal = right.evaluate(variables);

        switch (operator) {
            case "+": return leftVal + rightVal;
            case "-": return leftVal - rightVal;
            case "*": return leftVal * rightVal;
            case "/":
                if (rightVal == 0) throw new ArithmeticException("Division by zero");
                return leftVal / rightVal;
            case "^": return Math.pow(leftVal, rightVal);
            default: throw new IllegalArgumentException("Unknown operator: " + operator);
        }
    }
}

/**
 * FunctionNode 类表示函数调用节点<br>
 * 封装一个函数名（如sin、cos）和参数表达式
 */
class FunctionNode implements ExprNode {
    private final String functionName;
    private final ExprNode argument;

    /**
     * 构造函数
     * @param functionName 函数名称（如"sin", "cos"）
     * @param argument 函数参数表达式节点
     */
    public FunctionNode(String functionName, ExprNode argument) {
        this.functionName = functionName;
        this.argument = argument;
    }

    /**
     * 评估函数节点，根据函数名对参数进行相应的数学函数计算
     * @param variables 变量映射表，包含表达式计算所需的所有变量值
     * @return 函数计算的结果
     * @throws IllegalArgumentException 当函数名不被支持时抛出
     */
    @Override
    public double evaluate(Map<String, Double> variables) {
        double argValue = argument.evaluate(variables);

        switch (functionName) {
            case "sin": return Math.sin(argValue);
            case "cos": return Math.cos(argValue);
            default: throw new IllegalArgumentException("Unknown function: " + functionName);
        }
    }
}