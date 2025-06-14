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

    private final Map<String, Double> variables = new HashMap<>();

    public double parse(String expression) {
        String[] tokens = tokenize(expression);
        return evaluate(tokens);
    }

    private String[] tokenize(String expression) {
        Matcher matcher = PATTERN.matcher(expression);
        List<String> tokenList = new ArrayList<>();
        while (matcher.find()) {
            tokenList.add(matcher.group(1));
        }
        return tokenList.toArray(new String[0]);
    }

    private double evaluate(String[] tokens) {
        return parseExpression(tokens, new int[]{0});
    }

    private double parseExpression(String[] tokens, int[] index) {
        double left = parseTerm(tokens, index);
        while (index[0] < tokens.length) {
            String operator = tokens[index[0]];
            if (!operator.equals("+") && !operator.equals("-")) {
                break;
            }
            index[0]++;
            double right = parseTerm(tokens, index);
            if (operator.equals("+")) {
                left += right;
            } else {
                left -= right;
            }
        }
        return left;
    }

    private double parseTerm(String[] tokens, int[] index) {
        double left = parseFactor(tokens, index);
        while (index[0] < tokens.length) {
            String operator = tokens[index[0]];
            if (!operator.equals("*") && !operator.equals("/")) {
                break;
            }
            index[0]++;
            double right = parseFactor(tokens, index);
            if (operator.equals("*")) {
                left *= right;
            } else {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                left /= right;
            }
        }
        return left;
    }

    private double parseFactor(String[] tokens, int[] index) {
        if (index[0] >= tokens.length) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }
        String token = tokens[index[0]];
        index[0]++;
        double result;
        if (token.equals("(")) {
            result = parseExpression(tokens, index);
            if (index[0] >= tokens.length || !tokens[index[0]].equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            index[0]++;
        } else if (token.equals("sin")) {
            result = Math.sin(parseFactor(tokens, index));
        } else if (token.equals("cos")) {
            result = Math.cos(parseFactor(tokens, index));
        } else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            result = variables.getOrDefault(token, 0.0);
        } else {
            try {
                result = Double.parseDouble(token);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }
        if (index[0] < tokens.length && tokens[index[0]].equals("^")) {
            index[0]++;
            double exponent = parseFactor(tokens, index);
            result = Math.pow(result, exponent);
        }

        return result;
    }

    public void setVariable(String name, double value) {
        variables.put(name, value);
    }
}