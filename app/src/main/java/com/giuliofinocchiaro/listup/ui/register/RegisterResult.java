package com.giuliofinocchiaro.listup.ui.register;

import androidx.annotation.Nullable;

class RegisterResult {
    @Nullable private final Boolean success;
    @Nullable private final Integer error;

    RegisterResult(@Nullable Integer error) {
        this.error = error;
        this.success = null;
    }

    RegisterResult(@Nullable Boolean success) {
        this.success = success;
        this.error = null;
    }

    @Nullable public Boolean getSuccess() { return success; }
    @Nullable public Integer getError() { return error; }
}