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
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("."));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("1e3"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("()"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("2(3)"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate("2 & 3"));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluate(null));
    }

    @Test
    void acceptsLeadingAndTrailingDecimalPoint() {
        assertValue(0.5, ".5");
        assertValue(5, "5.");
        assertValue(1.75, ".5 + 1.25");
    }

    @Test
    void combinesUnaryAndBinaryOperators() {
        assertValue(5, "2 - -3");
        assertValue(-1, "2 + -3");
        assertValue(5, "2 + + 3");
        assertValue(6, "-2 * -3");
        assertValue(2, "-(-(2))");
        assertValue(-6, "-(2 * 3)");
    }

    @Test
    void handlesNestingAndLongChains() {
        assertValue(1, "(((1)))");
        assertValue(5050, "1+2+3+4+5+6+7+8+9+10+11+12+13+14+15+16+17+18+19+20+21+22+23+24+25+26+27+28+29+30+31+32+33+34+35+36"
                + "+37+38+39+40+41+42+43+44+45+46+47+48+49+50+51+52+53+54+55+56+57+58+59+60+61+62+63+64+65+66+67+68+69+70"
                + "+71+72+73+74+75+76+77+78+79+80+81+82+83+84+85+86+87+88+89+90+91+92+93+94+95+96+97+98+99+100");
        assertValue(1, "((((((((((1))))))))))");
    }

    @Test
    void tokenizesNumbersOperatorsAndParentheses() {
        assertEquals(List.of("(", "1.5", "+", "2", ")", "*", "3"), ExpressionEvaluator.tokenize("(1.5 + 2) * 3"));
        assertEquals(List.of("neg", "(", "2", ")"), ExpressionEvaluator.tokenize("-(2)"));
        assertEquals(List.of(), ExpressionEvaluator.tokenize("   "));
    }

    @Test
    void evaluatesPostfixDirectly() {
        assertEquals(11.0, ExpressionEvaluator.evaluatePostfix(List.of("3", "4", "2", "*", "+")), EPS);
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluatePostfix(List.of("+")));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluatePostfix(List.of("neg")));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluatePostfix(List.of("1", "2")));
        assertThrows(IllegalArgumentException.class, () -> ExpressionEvaluator.evaluatePostfix(List.of()));
    }

    @Test
    void rejectsResultsOutsideTheDoubleRange() {
        String big = "9".repeat(200);
        assertThrows(ArithmeticException.class,
                () -> ExpressionEvaluator.evaluate(big + " * " + big + " * " + big + " * " + big));
    }

    @Test
    void formatsResultsAsReadablePlainText() {
        assertEquals("5", ExpressionEvaluator.format(5.0));
        assertEquals("100", ExpressionEvaluator.format(100.0));
        assertEquals("0", ExpressionEvaluator.format(0.0));
        assertEquals("-2.5", ExpressionEvaluator.format(-2.5));
        assertEquals("0.00001", ExpressionEvaluator.format(1e-5));
        assertEquals("1000000000000000000000", ExpressionEvaluator.format(1e21));
    }

    @Test
    void formattedResultsCanBeEvaluatedAgain() {
        double value = ExpressionEvaluator.evaluate("1 / 3");
        assertEquals(value * 3, ExpressionEvaluator.evaluate(ExpressionEvaluator.format(value) + " * 3"), 1e-9);
        assertValue(0.00001, ExpressionEvaluator.format(1e-5));
    }
}
