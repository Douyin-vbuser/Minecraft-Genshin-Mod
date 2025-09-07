//本文件包含AI辅助生成内容
//AI辅助编程服务提供商:DeepSeek
//AI注释服务提供商:DeepSeek

package com.vbuser.particulate.math;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 表达式解析器类<br>
 * 负责将数学表达式字符串编译为抽象语法树（AST）并求值
 */
public class ExpressionParser {
    // 正则表达式模式，用于匹配表达式中的各种令牌（变量、数字、运算符、函数等）
    private static final Pattern PATTERN = Pattern.compile(
            "\\s*([a-zA-Z_][a-zA-Z0-9_]*(?:\\.?[a-zA-Z0-9_]+)*|\\d+(?:\\.\\d+)?|[+\\-*/()^]|sin|cos)\\s*");

    // 存储变量名和对应值的映射
    private final Map<String, Double> variables = new HashMap<>();

    /**
     * 编译表达式字符串为抽象语法树（AST）<br>
     * @param expression 要编译的数学表达式字符串
     * @return 表达式对应的抽象语法树根节点
     */
    public ExprNode compile(String expression) {
        String[] tokens = tokenize(expression);
        return parseExpression(tokens, new int[]{0});
    }

    /**
     * 解析并求值表达式字符串<br>
     * 使用内置变量映射进行求值
     * @param expression 要求值的数学表达式字符串
     * @return 表达式求值结果
     */
    public double parse(String expression) {
        ExprNode ast = compile(expression);
        return ast.evaluate(variables);
    }

    /**
     * 对已编译的抽象语法树进行求值<br>
     * 使用提供的变量映射进行求值
     * @param ast 已编译的抽象语法树
     * @param variables 变量名与值的映射
     * @return 表达式求值结果
     */
    public double evaluate(ExprNode ast, Map<String, Double> variables) {
        return ast.evaluate(variables);
    }

    /**
     * 将表达式字符串拆分为令牌数组<br>
     * 使用正则表达式匹配各种类型的令牌
     * @param expression 要拆分的表达式字符串
     * @return 令牌字符串数组
     */
    private String[] tokenize(String expression) {
        Matcher matcher = PATTERN.matcher(expression);
        List<String> tokenList = new ArrayList<>();
        while (matcher.find()) {
            tokenList.add(matcher.group(1));
        }
        return tokenList.toArray(new String[0]);
    }

    /**
     * 解析表达式（处理加减运算）<br>
     * 遵循运算符优先级，先解析项再处理加减运算
     * @param tokens 令牌数组
     * @param index 当前解析位置的索引（数组形式以便传递引用）
     * @return 表示表达式的抽象语法树节点
     */
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

    /**
     * 解析项（处理乘除运算）<br>
     * 遵循运算符优先级，先解析因子再处理乘除运算
     * @param tokens 令牌数组
     * @param index 当前解析位置的索引（数组形式以便传递引用）
     * @return 表示项的抽象语法树节点
     */
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

    /**
     * 解析因子（处理括号、函数、变量、数字和指数运算）<br>
     * 处理最高优先级的表达式元素
     * @param tokens 令牌数组
     * @param index 当前解析位置的索引（数组形式以便传递引用）
     * @return 表示因子的抽象语法树节点
     * @throws IllegalArgumentException 当遇到不匹配的括号或无效令牌时抛出
     */
    private ExprNode parseFactor(String[] tokens, int[] index) {
        if (index[0] >= tokens.length) {
            throw new IllegalArgumentException("Unexpected end of expression");
        }

        String token = tokens[index[0]];
        index[0]++;
        ExprNode result;

        // 处理括号表达式
        if (token.equals("(")) {
            result = parseExpression(tokens, index);
            if (index[0] >= tokens.length || !tokens[index[0]].equals(")")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            index[0]++;
        }
        // 处理函数调用（sin/cos）
        else if (token.equals("sin") || token.equals("cos")) {
            ExprNode argument = parseFactor(tokens, index);
            result = new FunctionNode(token, argument);
        }
        // 处理变量
        else if (token.matches("[a-zA-Z_][a-zA-Z0-9_]*")) {
            result = new VariableNode(token);
        }
        // 处理数字常量
        else {
            try {
                result = new NumberNode(Double.parseDouble(token));
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid token: " + token);
            }
        }

        // 处理指数运算（右结合）
        if (index[0] < tokens.length && tokens[index[0]].equals("^")) {
            index[0]++;
            ExprNode exponent = parseFactor(tokens, index);
            result = new BinaryOpNode(result, "^", exponent);
        }

        return result;
    }

    /**
     * 设置变量值<br>
     * 将变量名和对应值存入变量映射中
     * @param name 变量名
     * @param value 变量值
     */
    public void setVariable(String name, double value) {
        variables.put(name, value);
    }
}