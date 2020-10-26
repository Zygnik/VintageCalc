package com.example.simplecalculator;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.example.simplecalculator.NetworkUtils.APIClient;
import com.example.simplecalculator.NetworkUtils.LatestCurrencyResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

class CurrencyConverterDialog extends Dialog  implements View.OnClickListener {

    protected Activity activity;
    private TextInputEditText amountEditTxt;
    private AutoCompleteTextView fromTxtView, toTxtView;
    private TextInputLayout amountTxtInputLayout;

    private Double convertedValue;
    private OnDialogResult mDialogResult;

    public CurrencyConverterDialog(Context context) {
        super(context);
        this.activity = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.currency_converter_dialog);

        MaterialButton submitBtn = findViewById(R.id.btn_submit);
        MaterialButton cancelBtn = findViewById(R.id.btn_cancel);

        amountEditTxt = findViewById(R.id.amountEditTxt);
        fromTxtView = findViewById(R.id.fromTxtView);
        fromTxtView.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus){
                hideSoftKeyboard();
            }
        });
        toTxtView = findViewById(R.id.toTxtView);
        toTxtView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    hideSoftKeyboard();
                }
            }
        });
        submitBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);

        convertedValue = null;
        amountTxtInputLayout = findViewById(R.id.amountTxtInputLayout);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        activity,
                        R.layout.dropdown_currency_item,
                        activity.getResources().getStringArray(R.array.currencies));
        fromTxtView.setAdapter(adapter);
        toTxtView.setAdapter(adapter);
    }

    public void hideSoftKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(
                getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_submit:
                if (isNetworkAvailable()){
                    if (!Objects.requireNonNull(amountEditTxt.getText()).toString().matches("")){
                        StringBuilder symbols = new StringBuilder();
                        Double amount = Double.parseDouble(amountEditTxt.getText().toString());
                        String fromCurr = fromTxtView.getText().toString().substring(0,3);
                        String toCurr = toTxtView.getText().toString().substring(0, 3);
                        symbols.append(fromCurr);
                        symbols.append(", ");
                        symbols.append(toCurr);
                        sendLatestCurrenciesRequest(amount, fromCurr, toCurr, symbols.toString());
                    } else {
                        amountTxtInputLayout.setError(activity.getResources().getString(R.string.emptyFieldWarning));
                    }
                } else {
                    Toast.makeText(activity, activity.getResources().getString(R.string.noInternetConnection),
                            Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.btn_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null &&
                connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    private void sendLatestCurrenciesRequest(Double amount, String fromCurr, String toCurr, String symbols){
        Call<LatestCurrencyResponse> latestCurrencyResponseCall = APIClient.getLatestCurrencyService().
                getLatestCurrency(BuildConfig.fixerioKey, symbols);
        latestCurrencyResponseCall.enqueue(new Callback<LatestCurrencyResponse>() {
            @Override
            public void onResponse(@NotNull Call<LatestCurrencyResponse> call, @NotNull Response<LatestCurrencyResponse> response) {
                if (response.isSuccessful()){
                    new Handler().postDelayed(() -> {
                        LatestCurrencyResponse latestCurrencyResponse = response.body();
                        assert latestCurrencyResponse != null;
                        Boolean success = latestCurrencyResponse.getSuccess();
                        Map<String, Double> rates =  latestCurrencyResponse.getRates();
                        if (success && rates != null){
                            Double fromCurrValue = rates.get(fromCurr);
                            Double toCurrValue = rates.get(toCurr);
                            if (!(fromCurrValue == null) && !(toCurrValue == null)){
                                Double fromCurrValueToEUR = amount /fromCurrValue;
                                convertedValue = toCurrValue * fromCurrValueToEUR;
                            }
                            if (mDialogResult != null){
                                mDialogResult.onFinish(String.valueOf(convertedValue));
                            }
                        } else  {
                            Toast.makeText(activity, activity.getResources().getString(R.string.errorMessage1),
                                    Toast.LENGTH_LONG).show();
                        }
                    }, 700);
                } else{
                    Toast.makeText(activity, activity.getResources().getString(R.string.errorMessage1),
                            Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(@NotNull Call<LatestCurrencyResponse> call, @NotNull Throwable t) {
                Toast.makeText(activity, activity.getResources().getString(R.string.errorMessage2),
                        Toast.LENGTH_LONG).show();
                Log.i("Error", t.getLocalizedMessage());
            }
        });
    }

    public void setOnDialogResult(OnDialogResult dialogResult){
        mDialogResult = dialogResult;
    }

    public interface OnDialogResult{
        void onFinish(String result);
    }
}
