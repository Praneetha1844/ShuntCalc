package dev.praneetha.shuntcalc;

import java.util.Stack;

public class Main {
    boolean check(String expression) {
       
       

        boolean isValid = isExpressionValid(expression);
        return(isValid);
       

       
    }

    public static boolean isExpressionValid(String expression) {
        try {
            // Remove all spaces from the expression
            String cleanedExpression = expression.replaceAll("\\s+", "");
            double result = evaluateExpression(cleanedExpression);
            return true; // If the evaluation doesn't throw an exception, the expression is valid
        } catch (ArithmeticException | IllegalArgumentException e) {
            return false; // An exception occurred during evaluation, so the expression is invalid
        }
    }

    public static double evaluateExpression(String expression) {
        String postfix = infixToPostfix(expression);
        return evaluatePostfix(postfix);
    }

    public static String infixToPostfix(String expression) {
        StringBuilder postfix = new StringBuilder();
        Stack<Character> operatorStack = new Stack<>();

        int i = 0;
        while (i < expression.length()) {
            char c = expression.charAt(i);
            if (Character.isWhitespace(c)) {
                i++;
                continue;
            }

            if (Character.isDigit(c)) {
                // Read the entire number as a token and push it into the postfix output
                StringBuilder numBuilder = new StringBuilder();
                while (i < expression.length() && Character.isDigit(expression.charAt(i))) {
                    numBuilder.append(expression.charAt(i));
                    i++;
                }
                postfix.append(numBuilder).append(" ");
            } else if (isOperator(c)) {
                while (!operatorStack.isEmpty() && hasHigherPrecedence(operatorStack.peek(), c)) {
                    postfix.append(operatorStack.pop()).append(" ");
                }
                operatorStack.push(c);
                i++;
            } else if (c == '(') {
                operatorStack.push(c);
                i++;
            } else if (c == ')') {
                while (!operatorStack.isEmpty() && operatorStack.peek() != '(') {
                    postfix.append(operatorStack.pop()).append(" ");
                }
                if (!operatorStack.isEmpty() && operatorStack.peek() == '(') {
                    operatorStack.pop();
                }
                i++;
            }
        }

        while (!operatorStack.isEmpty()) {
            postfix.append(operatorStack.pop()).append(" ");
        }

        return postfix.toString().trim();
    }

    public static double evaluatePostfix(String postfix) {
        Stack<Double> operandStack = new Stack<>();
        String[] tokens = postfix.split("\\s+");

        for (String token : tokens) {
            if (token.matches("\\d+")) {
                operandStack.push(Double.parseDouble(token));
            } else if (isOperator(token.charAt(0))) {
                if (operandStack.size() < 2) {
                    throw new IllegalArgumentException("Invalid expression: Not enough operands to perform operation");
                }
                double operand2 = operandStack.pop();
                double operand1 = operandStack.pop();
                double result = performOperation(operand1, operand2, token.charAt(0));
                operandStack.push(result);
            }
        }

        if (operandStack.size() == 1) {
            return operandStack.pop();
        } else {
            throw new IllegalArgumentException("Invalid expression: Extra operands or insufficient operands");
        }
    }

    public static boolean isOperator(char c) {
        return c == '+' || c == '-' || c == '*' || c == '/';
    }

    public static boolean hasHigherPrecedence(char op1, char op2) {
        int precedenceOp1 = getPrecedence(op1);
        int precedenceOp2 = getPrecedence(op2);
        return precedenceOp1 >= precedenceOp2;
    }

    public static int getPrecedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case '*':
            case '/':
                return 2;
            default:
                return 0;
        }
    }

    public static double performOperation(double operand1, double operand2, char operator) {
        switch (operator) {
            case '+':
                return operand1 + operand2;
            case '-':
                return operand1 - operand2;
            case '*':
                return operand1 * operand2;
            case '/':
                if (operand2 == 0) {
                    throw new ArithmeticException("Division by zero is not allowed");
                }
                return operand1 / operand2;
            default:
                throw new IllegalArgumentException("Invalid operator");
        }
    }
}
