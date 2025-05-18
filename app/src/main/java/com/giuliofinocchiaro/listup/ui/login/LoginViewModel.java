package com.giuliofinocchiaro.listup.ui.login;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.giuliofinocchiaro.listup.R;
import com.giuliofinocchiaro.listup.data.Result;
import com.giuliofinocchiaro.listup.data.model.User;
import com.giuliofinocchiaro.listup.data.repository.LoginRepository;
import com.giuliofinocchiaro.listup.data.source.auth.LoginDataSource;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository loginRepository;

    LoginViewModel(Application application) {
        this.loginRepository = LoginRepository.getInstance(new LoginDataSource(), application);
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    public void login(String username, String password) {
        // can be launched in a separate asynchronous job
        //Result<User> result = loginRepository.login(username, password);
        loginRepository.login(username, password, new LoginRepository.LoginCallback() {
            @Override
            public void onSuccess(Result.Success<User> result) {
                User data = result.getData();
                loginResult.postValue(new LoginResult(new LoggedInUserView(data.getUsername())));
            }

            @Override
            public void onError(Result.Error error) {
                loginResult.postValue(new LoginResult(R.string.login_failed));
            }
        });

    }

    public boolean isLoggedIn() {
        return loginRepository.isLoggedIn();
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 5;
    }
}