package com.example.simplecalculator;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private double leftHandSide=0.0;
    private double rightHandSide=0.0;
    private Character operation=' ';
    private DecimalFormat format;
    private StringBuilder digitsCurrentlyCalculated = new StringBuilder();
    private String DEFAULT_FORMAT_PATTERN;

    TextView resultView;
    Button one, two, three, four, five, six, seven, eight, nine, zero, dot,
            additionBtn, subtractBtn, multiplyBtn, divideBtn, clearEverythingBtn,
            currencyConverterBtn, equalBtn, backspaceBtn;
    private Boolean operationJustExecuted, operatorSelected, operatorJustDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultView = findViewById(R.id.result_id);

        one = findViewById(R.id.one_btn);
        two = findViewById(R.id.two_btn);
        three = findViewById(R.id.three_btn);
        four = findViewById(R.id.four_btn);
        five = findViewById(R.id.five_btn);
        six = findViewById(R.id.six_btn);
        seven = findViewById(R.id.seven_btn);
        eight = findViewById(R.id.eight_btn);
        nine = findViewById(R.id.nine_btn);
        zero = findViewById(R.id.zero_btn);
        dot = findViewById(R.id.dot_btn);

        additionBtn = findViewById(R.id.addition_btn);
        subtractBtn = findViewById(R.id.subtract_btn);
        multiplyBtn = findViewById(R.id.multiply_btn);
        divideBtn = findViewById(R.id.divide_btn);

        clearEverythingBtn = findViewById(R.id.clear_everything_btn);
        backspaceBtn = findViewById(R.id.backspace_btn);
        equalBtn = findViewById(R.id.equal_btn);

        currencyConverterBtn = findViewById(R.id.currency_converter_btn);

        operationJustExecuted = false;
        operatorSelected = false;
        operatorJustDeleted = false;

        initializeButtons();

        // Format for customizing how result will appear
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        format.setDecimalFormatSymbols(decimalFormatSymbols);
        DEFAULT_FORMAT_PATTERN = format.toPattern();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putCharSequence("screenDigits", resultView.getText());
        outState.putSerializable("digitsCurrentlyCalculated", digitsCurrentlyCalculated);
        outState.putChar("operation", operation);
        outState.putBoolean("operationJustExecuted", operationJustExecuted);
        outState.putBoolean("operationJustDeleted", operatorJustDeleted);
        outState.putBoolean("operatorSelected", operatorSelected);
        outState.putDouble("leftHandSide", leftHandSide);
        outState.putDouble("rightHandSide", rightHandSide);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        resultView.setText(savedInstanceState.getCharSequence("screenDigits"));
        digitsCurrentlyCalculated = (StringBuilder) savedInstanceState.getSerializable("digitsCurrentlyCalculated");
        operation = savedInstanceState.getChar("operation");
        operationJustExecuted = savedInstanceState.getBoolean("operationJustExecuted");
        operatorSelected = savedInstanceState.getBoolean("operatorSelected");
        operatorJustDeleted = savedInstanceState.getBoolean("operatorJustDeleted");
        leftHandSide = savedInstanceState.getDouble("leftHandSide");
        rightHandSide = savedInstanceState.getDouble("rightHandSide");
    }

    private void initializeButtons(){
        functionalButtons();
        operationalButtons();
        numericalButtons();
    }

    private void numericalButtons(){
        one.setOnClickListener(v -> addDigitOnScreen("1"));
        two.setOnClickListener(v -> addDigitOnScreen("2"));
        three.setOnClickListener(v -> addDigitOnScreen("3"));
        four.setOnClickListener(v -> addDigitOnScreen("4"));
        five.setOnClickListener(v -> addDigitOnScreen("5"));
        six.setOnClickListener(v -> addDigitOnScreen("6"));
        seven.setOnClickListener(v -> addDigitOnScreen("7"));
        eight.setOnClickListener(v -> addDigitOnScreen("8"));
        nine.setOnClickListener(v -> addDigitOnScreen("9"));
        zero.setOnClickListener(v -> addDigitOnScreen("0"));
        dot.setOnClickListener(v -> addDigitOnScreen("."));
    }

    private void addDigitOnScreen(String digit){
        /* There are two scenarios when pressing a digit button. The first is that a button
        is pressed when the user hasn't calculated the result yet. The second scenario is
        that user press a digit button while the result of the previous operation is displayed
        on screen. The flag operationJustExecuted helps identify those scenarios.
         */
        /* The check !(digit.equals(".") && digitsOnScreen.toString().contains(".")) makes sure
        that the user won't press "." button when
         */
        if (!operationJustExecuted && !(digit.equals(".") && digitsCurrentlyCalculated.toString().replace(".0", "").contains("."))){
            if (operatorJustDeleted){
                digitsCurrentlyCalculated.setLength(0);
                digitsCurrentlyCalculated.append(resultView.getText());
                operatorJustDeleted = false;
            }
            digitsCurrentlyCalculated.append(digit);
            resultView.append(digit);
        }
        if (operationJustExecuted) {
            resetVariables();
            digitsCurrentlyCalculated.append(digit);
            resultView.setText(digit);
        }
    }

    private void operationalButtons(){
        additionBtn.setOnClickListener(v -> selectOperation('+'));
        subtractBtn.setOnClickListener(v -> selectOperation('-'));
        multiplyBtn.setOnClickListener(v -> selectOperation('*'));
        divideBtn.setOnClickListener(v -> selectOperation('/'));
    }

    private void selectOperation(Character chr){
        // Check if user deleted operator character and immediately pressed another operator
        if (operatorJustDeleted){
            digitsCurrentlyCalculated.setLength(0);
            digitsCurrentlyCalculated.append(resultView.getText());
            operatorJustDeleted = false;
        }
        // Check if leftSide number is not null and a decimal separator
        if (digitsCurrentlyCalculated.length() > 0 && digitsCurrentlyCalculated.charAt(digitsCurrentlyCalculated.length()-1) != 'e' && !(digitsCurrentlyCalculated.toString().equals(".")
                || digitsCurrentlyCalculated.toString().equals("-"))){
            double number = Double.parseDouble(digitsCurrentlyCalculated.toString());
            // Scenario in which user makes one operation after the other.
            if (!(leftHandSide == 0.0)){
                switch (operation) {
                    case '+':
                        leftHandSide = leftHandSide + number;
                        break;
                    case '-':
                        leftHandSide = leftHandSide - number;
                        break;
                    case '*':
                        leftHandSide = leftHandSide * number;
                        break;
                    case '/':
                        if (number != 0) {
                            leftHandSide = leftHandSide / number;
                        } else {
                            resultView.setText(R.string.divideByZero);
                            resetSides();
                        }
                        break;
                }
            } else {
                leftHandSide = number;
            }
            operation = chr;
            operatorSelected = true;
            operationJustExecuted = false;
            digitsCurrentlyCalculated.setLength(0);
            resultView.setText(format.format(leftHandSide) + chr.toString());
        } else if (digitsCurrentlyCalculated.length() == 0 && chr.toString().equals("-")){
            //User selects minus(-) on an empty screen
            digitsCurrentlyCalculated.append(chr);
            operationJustExecuted = false;
            resultView.setText(chr.toString());
        }
    }

    private void functionalButtons(){
        clearEverythingBtn.setOnClickListener(v -> {
            resultView.setText("");
            resetVariables();
            operatorSelected=false;
            operatorJustDeleted=false;
        });
        backspaceBtn.setOnClickListener(v -> clearDigit());
        equalBtn.setOnClickListener(v -> executeOperation());
    }

    private void executeOperation(){
        // Checks in order operation can be executed correctly
        // If one of these checks does not meet criteria the operation is not executed
        if (digitsCurrentlyCalculated.length() > 0 && !digitsCurrentlyCalculated.toString().equals(".")
                && !operationJustExecuted && operatorSelected){
            rightHandSide = Double.parseDouble(digitsCurrentlyCalculated.toString());
            digitsCurrentlyCalculated.setLength(0);
            operationJustExecuted = true;
            operatorSelected = false;
            switch (operation){
                case '+':
                    double sum = leftHandSide + rightHandSide;
                    defineResult(sum);
                    break;
                case '-':
                    double subtract = leftHandSide - rightHandSide;
                    defineResult(subtract);
                    break;
                case '*':
                    double multiply = leftHandSide * rightHandSide;
                    defineResult(multiply);
                    break;
                case '/':
                    if (rightHandSide != 0){
                        double divide = leftHandSide / rightHandSide;
                        defineResult(divide);
                        break;
                    } else {
                        resultView.setText(R.string.divideByZero);
                        digitsCurrentlyCalculated.setLength(0);
                        resetSides();
                    }
            }
        }
    }

    private void defineResult(Double result){
        resetSides();
        if (result.toString().length() > 15){
            format.applyPattern("##.###E0");
        }
        resultView.setText(format.format(result));
        digitsCurrentlyCalculated.append(result);
    }

    private void resetVariables(){
        resetSides();
        format.applyPattern(DEFAULT_FORMAT_PATTERN);
        digitsCurrentlyCalculated.setLength(0);
        operationJustExecuted=false;
    }

    private void resetSides(){
        leftHandSide = 0.0;
        rightHandSide = 0.0;
    }

    private void clearDigit(){
        // Check if user pressed clear button immediately after equal button
        if (operationJustExecuted){
            resetVariables();
            resultView.setText("");
        } else {
            int length = resultView.getText().length();
            if (resultView.getText().charAt(length-1) == '+' ||
                    resultView.getText().charAt(length-1) == '-' ||
                    resultView.getText().charAt(length-1) == '*' ||
                    resultView.getText().charAt(length-1) == '/'){
                operatorSelected = false;
                operatorJustDeleted = true;
            }
            if (length > 0){
                resultView.setText(resultView.getText().subSequence(0, length-1));
                int length2 = digitsCurrentlyCalculated.length();
                if (length2 == 0){
                    resetVariables();
                } else {
                    digitsCurrentlyCalculated.deleteCharAt(length2-1);
                }
            } else {
                resetVariables();
                resultView.setText("");
                operatorJustDeleted=false;
                operatorSelected=false;
            }
        }
    }
}