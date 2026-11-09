package dev.praneetha.shuntcalc;

import java.math.BigDecimal;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/**
 * Evaluates infix arithmetic expressions with the shunting-yard algorithm.
 *
 * <p>Pipeline: {@link #tokenize} -> {@link #toPostfix} -> {@link #evaluatePostfix}.
 * Supports {@code + - * /}, parentheses, decimal numbers (including {@code .5} and {@code 5.})
 * and unary minus/plus.
 */
public final class ExpressionEvaluator {

    /** Internal token for unary minus, distinct from the binary {@code -}. */
    static final String NEGATE = "neg";

    private ExpressionEvaluator() {
    }

    /**
     * Evaluates an infix expression.
     *
     * @throws IllegalArgumentException if the expression is malformed
     * @throws ArithmeticException      on division by zero or a result outside the double range
     */
    public static double evaluate(String expression) {
        return evaluatePostfix(toPostfix(expression));
    }

    /** Splits an expression into number, operator and parenthesis tokens. */
    static List<String> tokenize(String expression) {
        if (expression == null) {
            throw new IllegalArgumentException("Expression must not be null");
        }
        List<String> tokens = new ArrayList<>();
        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
            } else if (Character.isDigit(c) || c == '.') {
                int start = i;
                while (i < expression.length()
                        && (Character.isDigit(expression.charAt(i)) || expression.charAt(i) == '.')) {
                    i++;
                }
                String number = expression.substring(start, i);
                if (!number.matches("\\d+\\.?\\d*|\\.\\d+")) {
                    throw new IllegalArgumentException("Invalid number: " + number);
                }
                tokens.add(number);
            } else if (c == '+' || c == '-') {
                boolean unary = tokens.isEmpty() || isOperator(last(tokens)) || last(tokens).equals("(");
                if (!unary) {
                    tokens.add(String.valueOf(c));
                } else if (c == '-') {
                    tokens.add(NEGATE);
                } // unary plus changes nothing, so it is dropped
                i++;
            } else if (c == '*' || c == '/' || c == '(' || c == ')') {
                tokens.add(String.valueOf(c));
                i++;
            } else {
                throw new IllegalArgumentException("Unexpected character '" + c + "'");
            }
        }
        return tokens;
    }

    /** Converts an infix expression to postfix (reverse Polish) tokens. */
    public static List<String> toPostfix(String expression) {
        List<String> output = new ArrayList<>();
        Deque<String> operators = new ArrayDeque<>();

        for (String token : tokenize(expression)) {
            if (isNumber(token)) {
                output.add(token);
            } else if (token.equals("(")) {
                operators.push(token);
            } else if (token.equals(")")) {
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    output.add(operators.pop());
                }
                if (operators.isEmpty()) {
                    throw new IllegalArgumentException("Mismatched parentheses");
                }
                operators.pop(); // discard "("
            } else {
                while (!operators.isEmpty() && !operators.peek().equals("(")
                        && popsBefore(operators.peek(), token)) {
                    output.add(operators.pop());
                }
                operators.push(token);
            }
        }

        while (!operators.isEmpty()) {
            String operator = operators.pop();
            if (operator.equals("(")) {
                throw new IllegalArgumentException("Mismatched parentheses");
            }
            output.add(operator);
        }
        return output;
    }

    /** Evaluates postfix tokens produced by {@link #toPostfix}. */
    public static double evaluatePostfix(List<String> postfix) {
        Deque<Double> operands = new ArrayDeque<>();
        for (String token : postfix) {
            if (isNumber(token)) {
                operands.push(Double.parseDouble(token));
            } else if (token.equals(NEGATE)) {
                if (operands.isEmpty()) {
                    throw new IllegalArgumentException("Missing operand for unary minus");
                }
                operands.push(-operands.pop());
            } else {
                if (operands.size() < 2) {
                    throw new IllegalArgumentException("Not enough operands for '" + token + "'");
                }
                double right = operands.pop();
                double left = operands.pop();
                operands.push(apply(token.charAt(0), left, right));
            }
        }
        if (operands.size() != 1) {
            throw new IllegalArgumentException("Invalid expression");
        }
        double result = operands.pop();
        if (Double.isInfinite(result) || Double.isNaN(result)) {
            throw new ArithmeticException("Result out of range");
        }
        return result;
    }

    /**
     * Formats a result as plain decimal text that {@link #evaluate} can read back,
     * without a trailing ".0" and without scientific notation.
     */
    public static String format(double value) {
        return BigDecimal.valueOf(value).stripTrailingZeros().toPlainString();
    }

    private static double apply(char operator, double left, double right) {
        switch (operator) {
            case '+':
                return left + right;
            case '-':
                return left - right;
            case '*':
                return left * right;
            case '/':
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                return left / right;
            default:
                throw new IllegalArgumentException("Unknown operator '" + operator + "'");
        }
    }

    /** True if the operator on the stack must be emitted before pushing {@code incoming}. */
    private static boolean popsBefore(String onStack, String incoming) {
        int stackPrecedence = precedence(onStack);
        int incomingPrecedence = precedence(incoming);
        boolean rightAssociative = incoming.equals(NEGATE);
        return stackPrecedence > incomingPrecedence
                || (stackPrecedence == incomingPrecedence && !rightAssociative);
    }

    private static int precedence(String operator) {
        switch (operator) {
            case NEGATE:
                return 3;
            case "*":
            case "/":
                return 2;
            default:
                return 1;
        }
    }

    private static boolean isOperator(String token) {
        return token.equals(NEGATE) || (token.length() == 1 && "+-*/".contains(token));
    }

    private static boolean isNumber(String token) {
        return Character.isDigit(token.charAt(0));
    }

    private static String last(List<String> tokens) {
        return tokens.get(tokens.size() - 1);
    }
}
