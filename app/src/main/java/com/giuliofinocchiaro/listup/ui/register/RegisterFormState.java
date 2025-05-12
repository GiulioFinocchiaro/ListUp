package com.giuliofinocchiaro.listup.ui.register;

import androidx.annotation.Nullable;

public class RegisterFormState {
    @Nullable private final Integer usernameError;
    @Nullable private final Integer passwordError;
    @Nullable private final Integer confirmPasswordError;
    @Nullable private final Integer emailError;
    @Nullable private final Integer nameError;
    @Nullable private final Integer surnameError;
    private boolean isDataValid;

    public RegisterFormState(@Nullable Integer usernameError, @Nullable Integer passwordError,
                             @Nullable Integer confirmPasswordError, @Nullable Integer emailError,
                             @Nullable Integer nameError, @Nullable Integer surnameError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.confirmPasswordError = confirmPasswordError;
        this.emailError = emailError;
        this.nameError = nameError;
        this.surnameError = surnameError;
        this.isDataValid = false;
    }

    public RegisterFormState(boolean isDataValid) {
        this(null, null, null, null, null, null);
        this.isDataValid = isDataValid;
    }

    @Nullable public Integer getUsernameError() { return usernameError; }
    @Nullable public Integer getPasswordError() { return passwordError; }
    @Nullable public Integer getConfirmPasswordError() { return confirmPasswordError; }
    @Nullable public Integer getEmailError() { return emailError; }
    @Nullable public Integer getNameError() { return nameError; }
    @Nullable public Integer getSurnameError() { return surnameError; }
    public boolean isDataValid() { return isDataValid; }
}