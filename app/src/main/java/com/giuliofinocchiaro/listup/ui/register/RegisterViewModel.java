package com.giuliofinocchiaro.listup.ui.register;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.source.RegisterDataSource;

public class RegisterViewModel extends ViewModel {
    private final MutableLiveData<RegisterResult> registerResult = new MutableLiveData<>();
    private final MutableLiveData<RegisterFormState> registerFormState = new MutableLiveData<>();

    public LiveData<RegisterResult> getRegisterResult() {
        return registerResult;
    }
    public LiveData<RegisterFormState> getRegisterFormState() { return registerFormState; }

    public void registerUser(String username, String password, String confirmPassword, String email, String name, String surname) {
        RegisterDataSource registerDataSource = new RegisterDataSource();
        registerDataSource.register(username, password, email, name, surname, new RegisterDataSource.RegisterCallback() {
            @Override
            public void onSuccess(Result.Success<Boolean> result) {
                registerResult.setValue(new RegisterResult(true));
            }

            @Override
            public void onError(Result.Error error) {
                registerResult.setValue(new RegisterResult(R.string.register_failed));
            }
        });
    }

    public void registerDataChanged(String username, String password, String confirmPassword, String email, String name, String surname) {
        if (!isUserNameValid(username)) {
            registerFormState.setValue(new RegisterFormState(R.string.invalid_username, null, null, null, null, null));
        } else if (!isPasswordValid(password)) {
            registerFormState.setValue(new RegisterFormState(null, R.string.invalid_password, null, null, null, null));
        } else if (!isPasswordNotDifferent(password, confirmPassword)){
            registerFormState.setValue(new RegisterFormState(null, null, R.string.password_different, null, null, null));
        } else if (!isEmailValid(email)){
            registerFormState.setValue(new RegisterFormState(null, null, null, R.string.invalid_email, null, null));
        } else if (!isNameValid(name)){
            registerFormState.setValue(new RegisterFormState(null, null, null, null, R.string.invalid_name, null));
        } else if (!isSurnameValid(surname)){
            registerFormState.setValue(new RegisterFormState(null, null, null, null, null, R.string.invalid_surname));
        } else {
            registerFormState.setValue(new RegisterFormState(true));
        }
    }

    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        } else {
            return !username.trim().isEmpty();
        }
    }

    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }

    private boolean isPasswordNotDifferent(String password, String confirmPassword){
        return password.equals(confirmPassword);
    }

    private boolean isEmailValid(String email){
        if (email == null) return false;
        return !email.trim().isEmpty() && email.contains("@");
    }

    private boolean isNameValid(String name){
        return name != null && !name.trim().isEmpty();
    }

    private boolean isSurnameValid(String surname){
        return surname != null && !surname.trim().isEmpty();
    }
}