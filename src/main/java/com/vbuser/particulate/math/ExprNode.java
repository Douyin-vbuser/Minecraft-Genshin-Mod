package com.vbuser.particulate.math;

import java.util.Map;

public interface ExprNode {
    double evaluate(Map<String, Double> variables);
}

class NumberNode implements ExprNode {
    private final double value;

    public NumberNode(double value) {
        this.value = value;
    }

    @Override
    public double evaluate(Map<String, Double> variables) {
        return value;
    }
}

class VariableNode implements ExprNode {
    private final String name;

    public VariableNode(String name) {
        this.name = name;
    }

    @Override
    public double evaluate(Map<String, Double> variables) {
        return variables.getOrDefault(name, 0.0);
    }
}

class BinaryOpNode implements ExprNode {
    private final ExprNode left;
    private final ExprNode right;
    private final String operator;

    public BinaryOpNode(ExprNode left, String operator, ExprNode right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

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

class FunctionNode implements ExprNode {
    private final String functionName;
    private final ExprNode argument;

    public FunctionNode(String functionName, ExprNode argument) {
        this.functionName = functionName;
        this.argument = argument;
    }

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
