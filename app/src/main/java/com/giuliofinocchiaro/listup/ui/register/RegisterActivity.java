package com.giuliofinocchiaro.listup.ui.register;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.ui.tools.ToolbarHelper;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private TextInputEditText username, password, confirmPassword, name, surname, email;
    private Button registerButton;
    private RegisterViewModel registerViewModel;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.usernameRegister);
        password = findViewById(R.id.passwordRegister);
        confirmPassword = findViewById(R.id.passwordConfirm);
        name = findViewById(R.id.nameRegister);
        surname = findViewById(R.id.surnameRegister);
        email = findViewById(R.id.emailRegister);
        registerButton = findViewById(R.id.registerButton);

        registerViewModel = new ViewModelProvider(this, new RegisterViewModelFactory(this))
                .get(RegisterViewModel.class);

        Toolbar toolbar = findViewById(R.id.custom_toolbar_r);

        // Set up the Toolbar using ToolbarHelper
        ToolbarHelper.setupToolbar(toolbar, "Registrazione", this);

        ImageView tool = findViewById(R.id.toolbar_back);

        tool.setOnClickListener(v -> {
            finish();
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerViewModel.registerUser(username.getText().toString(), password.getText().toString(), confirmPassword.getText().toString(), email.getText().toString(), name.getText().toString(), surname.getText().toString());
            }
        });

        registerViewModel.getRegisterFormState().observe(this, new Observer<RegisterFormState>() {
            @Override
            public void onChanged(@Nullable RegisterFormState registerFormState) {
                if (registerFormState == null){
                    return;
                }
                registerButton.setEnabled(registerFormState.isDataValid());
                if (registerFormState.getEmailError() != null){
                    email.setError(getString(registerFormState.getEmailError()));
                }
                if (registerFormState.getPasswordError() != null){
                    password.setError(getString(registerFormState.getPasswordError()));
                }
                if (registerFormState.getUsernameError() != null){
                    username.setError(getString(registerFormState.getUsernameError()));
                }
                if (registerFormState.getConfirmPasswordError() != null){
                    confirmPassword.setError(getString(registerFormState.getConfirmPasswordError()));
                }
                if (registerFormState.getNameError() != null){
                    name.setError(getString(registerFormState.getNameError()));
                }
                if (registerFormState.getSurnameError() != null){
                    surname.setError(getString(registerFormState.getSurnameError()));
                }
            }
        });

        registerViewModel.getRegisterResult().observe(this, new Observer<RegisterResult>() {
            @Override
            public void onChanged(@Nullable RegisterResult registerResult) {
                if (registerResult == null){
                    return;
                }
                if (registerResult.getError() != null){
                    showRegisterFailed(registerResult.getError());
                    return;
                }
                if (registerResult.getSuccess() != null){
                    showRegisterSuccess();
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registerViewModel.registerDataChanged(username.getText().toString(), password.getText().toString(), confirmPassword.getText().toString(), email.getText().toString(), name.getText().toString(), surname.getText().toString());
            }
        };

        username.addTextChangedListener(afterTextChangedListener);
        password.addTextChangedListener(afterTextChangedListener);
        name.addTextChangedListener(afterTextChangedListener);
        surname.addTextChangedListener(afterTextChangedListener);
        email.addTextChangedListener(afterTextChangedListener);
        confirmPassword.addTextChangedListener(afterTextChangedListener);

        password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    registerViewModel.registerUser(username.getText().toString(), password.getText().toString(), confirmPassword.getText().toString(), email.getText().toString(), name.getText().toString(), surname.getText().toString());
                }
                return false;
            }
        });
    }

    private void showRegisterSuccess(){
        String success = getString(R.string.register_success);
        Toast.makeText(getApplicationContext(), success, Toast.LENGTH_SHORT).show();
        finish();
    }

    private void showRegisterFailed(@StringRes Integer s){
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
    }
}
