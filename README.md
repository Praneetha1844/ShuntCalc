# ShuntCalc

An infix expression calculator built on the **shunting-yard algorithm**, with a small AWT desktop front end.

## Features

- Operators `+ - * /` with correct precedence and left associativity
- Parentheses, including nesting
- Decimal numbers (`1.5 * 2.5`)
- Unary minus and plus (`-(2 + 3)`, `2 * -3`)
- Clear errors for malformed input, mismatched parentheses and division by zero
- Calculator window (AWT) with digit, operator and parenthesis keys

## How it works

```mermaid
flowchart LR
    A["Infix text<br/>3 + 4 * 2"] --> B[tokenize]
    B --> C["toPostfix<br/>shunting-yard"]
    C --> D["Postfix tokens<br/>3 4 2 * +"]
    D --> E[evaluatePostfix]
    E --> F["Result 11"]
    B -. invalid character or number .-> X[IllegalArgumentException]
    C -. mismatched parentheses .-> X
    E -. missing operands .-> X
    E -. divide by zero .-> Y[ArithmeticException]
```

1. **Tokenize** splits the text into numbers, operators and parentheses. A `-` at the start, after an operator or after `(` becomes an internal unary-negate token.
2. **Shunting-yard** converts the tokens to postfix using an operator stack. Precedence is `neg` (3), `* /` (2), `+ -` (1). Operators of equal precedence pop left to right. `neg` is right associative.
3. **Postfix evaluation** uses an operand stack. Any leftover or missing operand is rejected.

## Project structure

```
pom.xml
src/
  main/java/dev/praneetha/shuntcalc/
    ExpressionEvaluator.java   tokenizer, shunting-yard, postfix evaluator
    CalculatorApp.java         AWT front end
  test/java/dev/praneetha/shuntcalc/
    ExpressionEvaluatorTest.java
.github/workflows/ci.yml       build and test on every push
```

## Requirements

- JDK 17 or newer
- Maven 3.8 or newer

## Build, test and run

```bash
mvn -B verify                                   # compile and run the JUnit 5 tests
java -jar target/shuntcalc-1.0.0.jar            # open the calculator window
```

The jar manifest points at `dev.praneetha.shuntcalc.CalculatorApp`.

## Use as a library

```java
double value = ExpressionEvaluator.evaluate("(2 + 3) * 4"); // 20.0
```

## Tests

`ExpressionEvaluatorTest` covers precedence, associativity, parentheses, decimals, whitespace, unary operators, postfix output, division by zero, mismatched parentheses and malformed input.

## Limitations

- Only the four basic operators; no exponent, modulo or functions
- Uses `double`, so results carry floating-point rounding
- The AWT window has no keyboard input and no backspace key
- The window is not covered by automated tests

## Roadmap

- Exponent and modulo operators
- Keyboard input and a backspace key
- Optional `BigDecimal` mode

## License

MIT. See [LICENSE](LICENSE).
