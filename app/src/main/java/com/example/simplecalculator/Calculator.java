package com.example.simplecalculator;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Calculator {

    protected TextViewCallback textViewCallback;
    private double leftHandSide=0.0;
    private double rightHandSide=0.0;
    private Character operation=' ';
    protected StringBuilder digitsCurrentlyCalculated = new StringBuilder();
    private Boolean operationJustExecuted;

    protected DecimalFormat format;
    protected DecimalFormatSymbols screenDecimalFormatSymbols, doubleDecimalFormatSymbols;
    protected String DEFAULT_FORMAT_PATTERN;

    /*
    The main idea of calculator is that there are four main variables: digitsCurrentlyCalculated,
    leftHandSide and rightHandSide and operator. When user presses digits, these are added on
    digitsCurrentlyCalculated. When user presses an operator button the value on digitsCurrentlyCalculated
    is being moved to leftHandSide and the system expect user to press new digits in order to be added from
    the beginning on digitsCurrentlyCalculated. Then, if user presses again an operator button the leftHandSide is
    being updated with the result of the calculation between leftHandSide (previous operator) digitsCurrentlyCalculated.
    The screen will show this result and the new operator pressed. If the equal button is pressed, the result will be
    calculated in the same way and it will be displayed on screen.
     */

    public Calculator(TextViewCallback callback){
        this.textViewCallback = callback;
        operationJustExecuted = false;
        digitsCurrentlyCalculated.append(0);

        // Format for customizing how result will appear
        screenDecimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        screenDecimalFormatSymbols.setDecimalSeparator(',');
        screenDecimalFormatSymbols.setGroupingSeparator('.');

        doubleDecimalFormatSymbols = new DecimalFormatSymbols(Locale.getDefault());
        doubleDecimalFormatSymbols.setDecimalSeparator('.');

        format = new DecimalFormat();
        format.setDecimalSeparatorAlwaysShown(false);
        format.setDecimalFormatSymbols(screenDecimalFormatSymbols);
        DEFAULT_FORMAT_PATTERN = format.toPattern();
    }

    /*
    There are two scenarios when pressing a digit button. The first is that a button
    is pressed when the user hasn't calculated the result yet. The second scenario is
    that user press a digit button while the result of the previous operation is displayed
    on screen. The flag operationJustExecuted helps identify those scenarios.

    The check !(digit.equals(".") && digitsOnScreen.toString().contains(".")) makes sure
    that the user won't press "." button when there is already this character on screen.
    Also, digits are pressed only when screen has less than 15 characters.
    */
    public void addDigitsOnScreen (String digit){
        if (digitsCurrentlyCalculated.length() < 15){
            if (!operationJustExecuted && !(digit.equals(".")
                    && digitsCurrentlyCalculated.toString().contains("."))){
                // Check if an operation digit has been pressed before or not.
                if (operation == ' '){
                    // In order to add '.' character on digits, a check must be done in which the format changes temporarily to accept '.'
                    if (digit.equals(".")){
                        // Put zero in front of '.' if it is the first character pressed
                        if (digitsCurrentlyCalculated.length() == 0){
                            digitsCurrentlyCalculated.append(0);
                        }
                        digitsCurrentlyCalculated.append(digit);
                        // Make specific format before displaying result
                        format.setDecimalSeparatorAlwaysShown(true);
                        textViewCallback.seText(format.format(Double.parseDouble(digitsCurrentlyCalculated.toString())));
                        format.setDecimalSeparatorAlwaysShown(false);
                    } else {
                        digitsCurrentlyCalculated.append(digit);
                        textViewCallback.seText(format.format(Double.parseDouble(digitsCurrentlyCalculated.toString())));
                    }
                } else {
                    if (digit.equals(".")){
                        if (digitsCurrentlyCalculated.length() == 0){
                            digitsCurrentlyCalculated.append(0);
                        }
                        digitsCurrentlyCalculated.append(digit);
                        format.setDecimalSeparatorAlwaysShown(true);
                        // If operator character has already been pressed, display the previous number + operator + current digits
                        textViewCallback.seText(format.format(leftHandSide)
                                +operation+format.format(Double.parseDouble(digitsCurrentlyCalculated.toString())));
                        format.setDecimalSeparatorAlwaysShown(false);
                    } else {
                        digitsCurrentlyCalculated.append(digit);
                        textViewCallback.seText(format.format(leftHandSide)
                                +operation+format.format(Double.parseDouble(digitsCurrentlyCalculated.toString())));
                    }
                }

            }
            // Start from the beginning if an operation has just been completed and showing result
            if (operationJustExecuted) {
                resetVariables();
                digitsCurrentlyCalculated.append(digit);
                textViewCallback.seText(digitsCurrentlyCalculated.toString());
            }
        }
    }

    /*
    This function, with the help of the root if statement, will be executed only if user
    pressed some digits and these digits are not just '.' or '-'.

    Also, if user tries to make multiple calculations without pressing equal '=' button,
    the system will calculate the first result of the operation first and it will display
    the result and the second operation digit that was pressed.
    */
    public void selectOperation(Character chr){
        if (digitsCurrentlyCalculated.length() > 0 && !(digitsCurrentlyCalculated.toString().equals(".")
                || digitsCurrentlyCalculated.toString().equals("-"))) {
            double number = Double.parseDouble(digitsCurrentlyCalculated.toString());
            // Implementation of the scenario in which user makes one operation after the other.
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
                            textViewCallback.seText("Cannot divide by zero");
                            resetSides();
                        }
                        break;
                }
            } else {
                leftHandSide = number;
            }
            operation = chr;
            operationJustExecuted = false;
            digitsCurrentlyCalculated.setLength(0);
            textViewCallback.seText(format.format(leftHandSide) + chr.toString());
        }
    }

    /*
    The function, with the help of the root if statement, works only if there are numbers
    on digitsCurrentlyCalculated and there is no just '.' and  and an operation has been
    selected (operation != ' '). Also, with the help of operationJustExecuted, the system
    will not crash if user presses equal button two or more times.
     */
    public void executeOperation(){
        //
        if (digitsCurrentlyCalculated.length() > 0 && !digitsCurrentlyCalculated.toString().equals(".")
                && !operationJustExecuted && !(operation == ' ')){
            rightHandSide = Double.parseDouble(digitsCurrentlyCalculated.toString());
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
                        textViewCallback.seText("Cannot Divide by zero");
                        digitsCurrentlyCalculated.setLength(0);
                        resetSides();
                    }
            }
        }
    }

    /*
    Method to display result of operation on screen.
     */
    public void defineResult(Double result){
        resetSides();
        digitsCurrentlyCalculated.setLength(0);
        operationJustExecuted = true;
        operation = ' ';
        if (result.toString().length() > 18){
            format.applyPattern("#####.###E0");
            textViewCallback.seText(format.format(result));
            format.applyPattern(DEFAULT_FORMAT_PATTERN);
        } else {
            textViewCallback.seText(format.format(result));
        }
        digitsCurrentlyCalculated.append(result);
    }

    public void resetVariables(){
        resetSides();
        digitsCurrentlyCalculated.setLength(0);
        operationJustExecuted=false;
    }

    public void resetSides(){
        leftHandSide = 0.0;
        rightHandSide = 0.0;
    }

    public void clearEverything(){
        resetVariables();
        operation = ' ';
        digitsCurrentlyCalculated.append(0);
        textViewCallback.seText("0");
    }

    /*
    The function deletes a character from the screen. Because the screen shows a combination
    of the variables digitsCurrentlyCalculated, operation and leftHandSide, the function deletes first,
    if exist, the characters from digitsCurrentlyCalculated. If this is empty, it will check if there is
    an operation defined and it will delete it. If the operation is also empty, it will check leftHandSide
    and move the value to digitsCurrentlyCalculated. If all of these are empty, it will do nothing.
     */
    public void clearDigit(){
        // Check if user pressed clear button immediately after equal button
        if (operationJustExecuted){
            resetVariables();
            operation = ' ';
            digitsCurrentlyCalculated.append(0);
            textViewCallback.seText("0");
        } else {
            int length = digitsCurrentlyCalculated.length();
            // This is the case in which the screen displays for example "345.5+" and user wants to delete
            if (length == 0){
                // Move leftHandSide value to digitsCurrentlyCalculated if the second one is empty
                // and user deletes operation character from screen.
                if (!(operation == ' ')){
                    operation =  ' ';
                    format.setDecimalFormatSymbols(doubleDecimalFormatSymbols);
                    format.setGroupingUsed(false);
                    format.setDecimalSeparatorAlwaysShown(true);
                    // Transform double value manually in order to be stored without NumberFormat Error
                    // This is happening because format obj cannot format numbers without digits after '.'
                    // For example 251. (This has meaning as a Double however).
                    if (format.format(leftHandSide).charAt(format.format(leftHandSide).length()-1) == '.'){
                        digitsCurrentlyCalculated.append(format.format(leftHandSide).replace(".",""));
                    } else {
                        digitsCurrentlyCalculated.append(format.format(leftHandSide));
                    }
                    format.setDecimalSeparatorAlwaysShown(false);
                    format.setGroupingUsed(true);
                    format.setDecimalFormatSymbols(screenDecimalFormatSymbols);
                    leftHandSide = 0.0;
                    textViewCallback.seText(format.format(Double.parseDouble(digitsCurrentlyCalculated.toString())));
                } else {
                    resetVariables();
                    textViewCallback.seText("");
                }
            } else {
                // if leftHandSide is 0 then digitsCurrentlyCalculated represent first number of operation
                // if leftHandSide is not 0, digitsCurrentlyCalculated represent second number of operation
                if (leftHandSide == 0.0){
                    // Catch '.' character correctly and delete it
                    if (digitsCurrentlyCalculated.charAt(digitsCurrentlyCalculated.length()-1) == '.'){
                        digitsCurrentlyCalculated.deleteCharAt(length-1);
                        length = digitsCurrentlyCalculated.length();
                        digitsCurrentlyCalculated.deleteCharAt(length-1);
                    } else {
                        digitsCurrentlyCalculated.deleteCharAt(length-1);
                    }
                    // Check if there are no other digits to be deleted from screen
                    if (digitsCurrentlyCalculated.length() == 0){
                        digitsCurrentlyCalculated.append(0);
                        textViewCallback.seText(digitsCurrentlyCalculated.toString());
                    } else {
                        // Check if digitsCurrentlyCalculated is just '.' character and delete it
                        if (digitsCurrentlyCalculated.toString().equals(".")){
                            digitsCurrentlyCalculated.toString().replace(".", "");
                        }
                        textViewCallback.seText(format.format(Double.parseDouble(digitsCurrentlyCalculated.toString())));
                    }
                }
                //second number of operation case: leftHandSide contains data
                else {
                    // Catch '.' character correctly and delete it
                    if (digitsCurrentlyCalculated.charAt(digitsCurrentlyCalculated.length()-1) == '.'){
                        digitsCurrentlyCalculated.deleteCharAt(length-1);
                        length = digitsCurrentlyCalculated.length();
                        digitsCurrentlyCalculated.deleteCharAt(length-1);
                    } else {
                        digitsCurrentlyCalculated.deleteCharAt(length-1);
                    }
                    // If digitsCurrentlyCalculated is empty, display only leftHandSide and operation on screen
                    // This is happening in order to avoid ParseDouble Errors
                    if (digitsCurrentlyCalculated.length()==0){
                        textViewCallback.seText(format.format(leftHandSide)
                                +operation);
                    } else {
                        textViewCallback.seText(format.format(leftHandSide)
                                +operation+format.format(Double.parseDouble(digitsCurrentlyCalculated.toString())));
                    }
                }
            }
        }
    }
}
