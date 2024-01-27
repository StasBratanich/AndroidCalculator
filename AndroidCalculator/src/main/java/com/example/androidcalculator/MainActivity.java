package com.example.androidcalculator;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Stack;

public class MainActivity extends AppCompatActivity {

    private TextView currentNumber;
    private TextView result;
    private String operator = "";
    public boolean isEqualsClicked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentNumber = findViewById(R.id.textNumberInsert);
        result = findViewById(R.id.textNumberCurrent);
    }

    public void numberFunction(View view) {
        Button button = (Button) view;
        String clickBtn = button.getText().toString();

        if (isEqualsClicked) {
            isEqualsClicked = false;
        }

        String newText = currentNumber.getText().toString() + clickBtn;

        currentNumber.setText(newText);
    }

    public void operatorFunction(View view) {
        Button operatorButton = (Button) view;
        operator = operatorButton.getText().toString();
        String num1 = currentNumber.getText().toString();

        currentNumber.append(" " + operator + " ");
    }


    @SuppressLint("SetTextI18n")
    public void equalsFunction(View view) {

        String equation = "";

        if (!currentNumber.getText().toString().isEmpty())
        {
            equation += currentNumber.getText().toString();
        }
        else {
            equation = currentNumber.getText().toString();
        }

        if (!result.getText().toString().isEmpty())
        {
            result.setText("");
        }

        result.append(" " + equation + " =");
        currentNumber.setText("");

        try {
            Double calculatedResult = calculateResult(equation);
            @SuppressLint("DefaultLocale") String formattedResult = String.format("%.2f", calculatedResult);

            int lastIndex = formattedResult.length() - 1;
            while (lastIndex >= 0 && formattedResult.charAt(lastIndex) == '0') {
                lastIndex--;
            }
            if (lastIndex >= 0 && formattedResult.charAt(lastIndex) == '.') {
                lastIndex--;
            }
            formattedResult = formattedResult.substring(0, lastIndex + 1);

            currentNumber.setText(formattedResult);

            isEqualsClicked = true;
        } catch (NumberFormatException e) {
            result.setText("Error: Invalid input");
        }
    }

    private double calculateResult(String expression) {
        String[] parts = expression.split("\\s+");

        Stack<Double> numbers = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (String part : parts) {
            try {
                numbers.push(Double.parseDouble(part));
            } catch (NumberFormatException e) {
                char op = part.charAt(0);
                if (op == '(') {
                    operators.push(op);
                } else if (op == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        processOperator(numbers, operators);
                    }
                    operators.pop();
                } else {
                    while (!operators.isEmpty() && hasPrecedence(op, operators.peek())) {
                        processOperator(numbers, operators);
                    }
                    operators.push(op);
                }
            }
        }

        while (!operators.isEmpty()) {
            processOperator(numbers, operators);
        }

        return numbers.pop();
    }

    private boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        }

        int precedence1 = getPrecedence(op1);
        int precedence2 = getPrecedence(op2);

        return precedence1 >= precedence2;
    }

    private int getPrecedence(char op) {
        switch (op) {
            case '+':
            case '-':
                return 1;
            case 'X':
            case '/':
            case '%':
                return 2;
            default:
                return -1;
        }
    }

    private void processOperator(Stack<Double> numbers, Stack<Character> operators) {
        double num2 = numbers.pop();
        double num1 = numbers.pop();
        char op = operators.pop();
        double result = 0;

        switch (op) {
            case '+':
                result = num1 + num2;
                break;
            case '-':
                result = num1 - num2;
                break;
            case 'X':
                result = num1 * num2;
                break;
            case '%':
                result = num1 % num2;
                break;
            case '/':
                if (num2 == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                result = num1 / num2;
                break;
        }

        numbers.push(result);
    }

    public void deleteFunction(View view) {
        String currentText  = currentNumber.getText().toString();

        if(!currentText.isEmpty()) {
            String newText = currentText.substring(0, currentText.length() - 1);
            currentNumber.setText(newText);
        }
    }

    public void ceFunction(View view) {
        currentNumber.setText("");
        result.setText("");
    }

    public void dotFunction(View view)
    {
        currentNumber.append(".");
    }

    public void parenthesisFunction(View view) {
        String currentText = currentNumber.getText().toString();

        boolean hasOpenParen = currentText.contains("(");
        boolean hasTrailingSpace = currentText.endsWith("  ");

        if (hasOpenParen) {
            currentNumber.append(" )");
        } else if (!hasTrailingSpace) {
            currentNumber.append("( ");
        }
    }
}