package com.example.simplecalculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Calculator {

    private TextViewCallback callback;
    private double leftHandSide=0.0;
    private double rightHandSide=0.0;
    private Character operation=' ';
    private StringBuilder digitsCurrentlyCalculated = new StringBuilder();
    private Boolean operationJustExecuted, operatorSelected, operatorJustDeleted;
    private DecimalFormat format;
    private String DEFAULT_FORMAT_PATTERN;

    public Calculator(TextViewCallback callback){
        this.callback = callback;
        operationJustExecuted = false;
        operatorSelected = false;
        operatorJustDeleted = false;

        // Format for customizing how result will appear
        DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        decimalFormatSymbols.setDecimalSeparator('.');
        decimalFormatSymbols.setGroupingSeparator(',');
        format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        format.setDecimalFormatSymbols(decimalFormatSymbols);
        DEFAULT_FORMAT_PATTERN = format.toPattern();
    }

    public void addDigitsOnScreen (String digit){
        /* There are two scenarios when pressing a digit button. The first is that a button
        is pressed when the user hasn't calculated the result yet. The second scenario is
        that user press a digit button while the result of the previous operation is displayed
        on screen. The flag operationJustExecuted helps identify those scenarios.
         */
        /* The check !(digit.equals(".") && digitsOnScreen.toString().contains(".")) makes sure
        that the user won't press "." button when
         */
        if (!operationJustExecuted && !(digit.equals(".")
                && digitsCurrentlyCalculated.toString().replace(".0", "").contains("."))){
            if (operatorJustDeleted){
                digitsCurrentlyCalculated.setLength(0);
                digitsCurrentlyCalculated.append(callback.getText());
                operatorJustDeleted = false;
            }
            digitsCurrentlyCalculated.append(digit);
            callback.updateText(digit);
        }
        if (operationJustExecuted) {
            resetVariables();
            digitsCurrentlyCalculated.append(digit);
            callback.seText(digit);
        }
    }

    public void selectOperation(Character chr){
        // Check if user deleted operator character and immediately pressed another operator
        if (operatorJustDeleted){
            digitsCurrentlyCalculated.setLength(0);
            digitsCurrentlyCalculated.append(callback.getText());
            operatorJustDeleted = false;
        }
        // Check if leftSide number is not null and a decimal separator
        if (digitsCurrentlyCalculated.length() > 0 &&
                digitsCurrentlyCalculated.charAt(digitsCurrentlyCalculated.length()-1) != 'e'
                && !(digitsCurrentlyCalculated.toString().equals(".")
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
                            callback.seText("Cannot divide by zero");
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
            callback.seText(leftHandSide + chr.toString());
        } else if (digitsCurrentlyCalculated.length() == 0 && chr.toString().equals("-")){
            //User selects minus(-) on an empty screen
            digitsCurrentlyCalculated.append(chr);
            operationJustExecuted = false;
            callback.seText(chr.toString());
        }
    }

    public void executeOperation(){
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
                        callback.seText("Cannot Divide by zero");
                        digitsCurrentlyCalculated.setLength(0);
                        resetSides();
                    }
            }
        }
    }

    public void defineResult(Double result){
        resetSides();
        if (result.toString().length() > 20){
            //format.applyPattern("##.###E0");
        }
        callback.seText(format.format(result));
        digitsCurrentlyCalculated.append(result);
    }

    public void resetVariables(){
        resetSides();
        format.applyPattern(DEFAULT_FORMAT_PATTERN);
        digitsCurrentlyCalculated.setLength(0);
        operationJustExecuted=false;
    }

    public void resetSides(){
        leftHandSide = 0.0;
        rightHandSide = 0.0;
    }

    public void clearEverything(){
        resetVariables();
        operatorSelected=false;
        operatorJustDeleted=false;
    }

    public void clearDigit(){
        // Check if user pressed clear button immediately after equal button
        if (operationJustExecuted){
            resetVariables();
            callback.seText("");
        } else {
            int length = callback.getText().length();
            if (length > 0){
                if (callback.getText().charAt(length-1) == '+' ||
                        callback.getText().charAt(length-1) == '-' ||
                        callback.getText().charAt(length-1) == '*' ||
                        callback.getText().charAt(length-1) == '/'){
                    operatorSelected = false;
                    operatorJustDeleted = true;
                }
                callback.seText(callback.getText().subSequence(0, length-1).toString());
                int length2 = digitsCurrentlyCalculated.length();
                if (length2 == 0){
                    resetVariables();
                } else {
                    digitsCurrentlyCalculated.deleteCharAt(length2-1);
                }
            } else {
                resetVariables();
                callback.seText("");
                operatorJustDeleted=false;
                operatorSelected=false;
            }
        }
    }
}
