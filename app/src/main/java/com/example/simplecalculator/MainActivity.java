package com.example.simplecalculator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements TextViewCallback{

    Calculator calculator;
    TextView resultView;
    Button one, two, three, four, five, six, seven, eight, nine, zero, dot,
            additionBtn, subtractBtn, multiplyBtn, divideBtn, clearEverythingBtn,
            currencyConverterBtn, equalBtn, backspaceBtn;

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

        calculator = new Calculator(this);

        initializeButtons();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        calculator = new Calculator(this);
    }

    @Override
    public void seText(String text) {
        resultView.setText(text);
    }

    @Override
    public void updateText(String text) {
        resultView.append(text);
    }

    @Override
    public CharSequence getText() {
        return resultView.getText();
    }

    private void initializeButtons(){
        functionalButtons();
        operationalButtons();
        numericalButtons();
    }

    private void numericalButtons(){
        one.setOnClickListener(v -> calculator.addDigitsOnScreen("1"));
        two.setOnClickListener(v -> calculator.addDigitsOnScreen("2"));
        three.setOnClickListener(v -> calculator.addDigitsOnScreen("3"));
        four.setOnClickListener(v -> calculator.addDigitsOnScreen("4"));
        five.setOnClickListener(v -> calculator.addDigitsOnScreen("5"));
        six.setOnClickListener(v -> calculator.addDigitsOnScreen("6"));
        seven.setOnClickListener(v -> calculator.addDigitsOnScreen("7"));
        eight.setOnClickListener(v -> calculator.addDigitsOnScreen("8"));
        nine.setOnClickListener(v -> calculator.addDigitsOnScreen("9"));
        zero.setOnClickListener(v -> calculator.addDigitsOnScreen("0"));
        dot.setOnClickListener(v -> calculator.addDigitsOnScreen("."));
    }

    private void operationalButtons(){
        additionBtn.setOnClickListener(v -> calculator.selectOperation('+'));
        subtractBtn.setOnClickListener(v -> calculator.selectOperation('-'));
        multiplyBtn.setOnClickListener(v -> calculator.selectOperation('*'));
        divideBtn.setOnClickListener(v -> calculator.selectOperation('/'));
    }

    private void functionalButtons(){
        clearEverythingBtn.setOnClickListener(v -> {
            resultView.setText("");
            calculator.clearEverything();
        });
        backspaceBtn.setOnClickListener(v -> calculator.clearDigit());
        equalBtn.setOnClickListener(v -> calculator.executeOperation());
        currencyConverterBtn.setOnClickListener(v -> {
            CurrencyConverterDialog ccDialog = new CurrencyConverterDialog(this);
            ccDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            ccDialog.setOnDialogResult(dialogResult -> {
                seText(dialogResult);
                calculator.resetVariables();
                ccDialog.dismiss();
            });
            ccDialog.show();
        });
    }
}