package dev.praneetha.shuntcalc;

import java.awt.Button;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Minimal AWT front end for {@link ExpressionEvaluator}. */
public final class CalculatorApp extends Frame {

    private static final String[] KEYS = {
        "1", "2", "3",
        "4", "5", "6",
        "7", "8", "9",
        "(", "0", ")",
        "+", "-", "*",
        "/", "Clear", "=",
    };

    private final Label display = new Label("", Label.CENTER);
    private final StringBuilder expression = new StringBuilder();

    private CalculatorApp() {
        super("ShuntCalc");
        setLayout(new GridLayout(0, 3));
        setSize(300, 400);

        add(new Label());
        add(display);
        add(new Label());

        for (String key : KEYS) {
            Button button = new Button(key);
            button.addActionListener(this::onKey);
            add(button);
        }

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void onKey(ActionEvent event) {
        String key = ((Button) event.getSource()).getLabel();
        if (key.equals("Clear")) {
            expression.setLength(0);
            display.setText("");
        } else if (key.equals("=")) {
            evaluate();
        } else {
            expression.append(key);
            display.setText(expression.toString());
        }
    }

    private void evaluate() {
        try {
            double result = ExpressionEvaluator.evaluate(expression.toString());
            String text = format(result);
            expression.setLength(0);
            expression.append(text);
            display.setText(text);
        } catch (ArithmeticException | IllegalArgumentException e) {
            expression.setLength(0);
            display.setText("Error: " + e.getMessage());
        }
    }

    /** Shows whole numbers without a trailing ".0". */
    static String format(double value) {
        return value == Math.rint(value) && Math.abs(value) < 1e15
                ? String.valueOf((long) value)
                : String.valueOf(value);
    }

    public static void main(String[] args) {
        new CalculatorApp().setVisible(true);
    }
}
