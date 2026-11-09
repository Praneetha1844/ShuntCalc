package dev.praneetha.shuntcalc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import org.junit.jupiter.api.Test;

class ExpressionEvaluatorTest {

    private static final double EPS = 1e-9;

    private static void assertValue(double expected, String expression) {
        assertEquals(expected, ExpressionEvaluator.evaluate(expression), EPS, expression);
    }

    @Test
    void respectsOperatorPrecedence() {
        assertValue(14, "2 + 3 * 4");
        assertValue(10, "2 * 3 + 4");
    }

    @Test
    void isLeftAssociative() {
        assertValue(3, "8 - 3 - 2");
        assertValue(1, "8 / 4 / 2");
    }

    @Test
    void honoursParentheses() {
        assertValue(20, "(2 + 3) * 4");
        assertValue(26, "2 * ((3 + 4) * 2) - 2");
    }

    @Test
    void supportsDecimals() {
        assertValue(3.75, "1.5 * 2.5");
        assertValue(0.5, "1 / 2");
    }

    @Test
    void ignoresWhitespace() {
        assertValue(7, "  1+ 2 *  3 ");
    }

    @Test
    void supportsUnaryMinusAndPlus() {
        assertValue(-5, "-5");
        assertValue(-6, "-2 * 3");
        assertValue(-6, "2 * -3");
        assertValue(3, "--3");
        assertValue(-5, "-(2 + 3)");
        assertValue(5, "+5");
        assertValue(1, "(-2) + 3");
    }

    @Test
    void convertsToPostfix() {
        assertEquals(List.of("3", "4", "2", "*", "+"), ExpressionEvaluator.toPostfix("3 + 4 * 2"));
        assertEquals(List.of("2", "3", "+", "4", "*"), ExpressionEvaluator.toPostfix("(2 + 3) * 4"));
        assertEquals(List.of("2", "neg"), ExpressionEvaluator.toPostfix("-2"));
    }

    @Test
    void rejectsDivisionByZero() {
        assertThrows(ArithmeticException.class, () -> ExpressionEvaluator.evaluate("1 / 0"));
        assertThrows(ArithmeticException.class, () -> ExpressionEvaluator.evaluate("4 / (2 - 2)"));
    }

    @Test
    void rejectsMismatchedParentheses() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("(1 + 2"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("1 + 2)"));
    }

    @Test
    void rejectsMalformedInput() {
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate(""));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("1 +"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("* 2"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("2 3"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("1.2.3 + 1"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("2 & 3"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate(null));
    }
}
