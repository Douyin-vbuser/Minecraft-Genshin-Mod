package com.vbuser.particulate.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {
    private static final Pattern PATTERN = Pattern.compile(
            "\\s*([a-zA-Z_][a-zA-Z0-9_]*(?:\\.?[a-zA-Z0-9_]+)*|\\d+(?:\\.\\d+)?|[+\\-*/()^]|sin|cos)\\s*");

    public ExprNode compile(String expression) {
        String[] tokens = tokenize(expression);
        return parseExpression(tokens, new int[]{0});
    }

    public double parse(String expression) {
        ExprNode ast = compile(expression);
        return ast.evaluate(variables);
    }

    public double evaluate(ExprNode ast, Map<String, Double> variables) {
        return ast.evaluate(variables);
    }

    private String[] tokenize(String expression) {
        Matcher matcher = PATTERN.matcher(expression);
        List<String> tokenList = new ArrayList<>();
        while (matcher.find()) {
            tokenList.add(matcher.group(1));
        }
        return tokenList.toArray(new String[0]);
    }

    private ExprNode parseExpression(String[] tokens, int[] index) {
        ExprNode left = parseTerm(tokens, index);
        while (index[0] < tokens.length) {
            String operator = tokens[index[0]];
            if (!operator.equals("+") && !operator.equals("-")) {
                break;
            }
            index[0]++;
            ExprNode right = parseTerm(tokens, index);
            left = new BinaryOpNode(left, operator, right);
        }
        return left;
    }

    private ExprNode parseTerm(String[] tokens, int[] index) {
        ExprNode left = parseFactor(tokens, index);
        while (index[0] < tokens.length) {
            String operator = tokens[index[0]];
            if (!operator.equals("*") && !operator.equals("/")) {
                break;
            }
            index[0]++;
            ExprNode right = parseFactor(tokens, index);
            left = new BinaryOpNode(left, operator, right);
        }
        return left;
    }

    private ExprNode parseFactor(String[] tokens, int[] index) {
        if (index[0] >= tokens.length) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }

        String token = tokens[index[0]];
        index[0]++;
        ExprNode result;

        if (token.equals("(")) {
            result = parseExpression(tokens, index);
            if (index[0] >= tokens.length || !tokens[index[0]].equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            index[0]++;
        } else if (token.equals("sin") || token.equals("cos")) {
            ExprNode argument = parseFactor(tokens, index);
            result = new FunctionNode(token, argument);
        } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            result = new VariableNode(token);
        } else {
            try {
                result = new NumberNode(Double.parseDouble(token));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        if (index[0] < tokens.length && tokens[index[0]].equals("^")) {
            index[0]++;
            ExprNode exponent = parseFactor(tokens, index);
            result = new BinaryOpNode(result, "^", exponent);
        }

        return result;
    }

    private final Map<String, Double> variables = new HashMap<>();
    public void setVariable(String name, double value) {
        variables.put(name, value);
    }
}