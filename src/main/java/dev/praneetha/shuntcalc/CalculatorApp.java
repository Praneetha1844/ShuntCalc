package dev.praneetha.shuntcalc;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** Minimal AWT front end for {@link ExpressionEvaluator}. */
public final class CalculatorApp extends Frame {

    private static final String CLEAR = "Clear";
    private static final String DELETE = "Del";
    private static final String EQUALS = "=";

    private static final String[] KEYS = {
        "7", "8", "9", "/",
        "4", "5", "6", "*",
        "1", "2", "3", "-",
        "0", ".", "(", "+",
        ")", DELETE, CLEAR, EQUALS,
    };

    private static final String ALLOWED_TYPED = "0123456789.+-*/()";

    private final Label display = new Label("", Label.RIGHT);
    private final StringBuilder expression = new StringBuilder();

    private CalculatorApp() {
        super("ShuntCalc");
        setLayout(new BorderLayout());
        setSize(320, 420);

        display.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 20));
        add(display, BorderLayout.NORTH);

        Panel keypad = new Panel(new GridLayout(0, 4));
        for (String key : KEYS) {
            Button button = new Button(key);
            button.setFocusable(false); // keep keyboard focus on the frame
            button.addActionListener(this::onButton);
            keypad.add(button);
        }
        add(keypad, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                onKeyTyped(e.getKeyChar());
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                dispose();
            }
        });
    }

    private void onButton(ActionEvent event) {
        press(((Button) event.getSource()).getLabel());
    }

    private void onKeyTyped(char c) {
        if (c == '\n' || c == '=') {
            press(EQUALS);
        } else if (c == '\b') {
            press(DELETE);
        } else if (c == 27) { // Escape
            press(CLEAR);
        } else if (ALLOWED_TYPED.indexOf(c) >= 0) {
            press(String.valueOf(c));
        }
    }

    private void press(String key) {
        switch (key) {
            case CLEAR:
                expression.setLength(0);
                break;
            case DELETE:
                if (expression.length() > 0) {
                    expression.setLength(expression.length() - 1);
                }
                break;
            case EQUALS:
                evaluate();
                return;
            default:
                expression.append(key);
                break;
        }
        display.setText(expression.toString());
    }

    private void evaluate() {
        try {
            String text = ExpressionEvaluator.format(ExpressionEvaluator.evaluate(expression.toString()));
            expression.setLength(0);
            expression.append(text);
            display.setText(text);
        } catch (ArithmeticException | IllegalArgumentException e) {
            expression.setLength(0);
            display.setText("Error: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // AWT components must be created and shown on the event dispatch thread.
        EventQueue.invokeLater(() -> new CalculatorApp().setVisible(true));
    }
}
