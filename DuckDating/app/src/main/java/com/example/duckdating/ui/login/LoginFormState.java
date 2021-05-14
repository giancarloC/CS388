package com.example.duckdating.ui.login;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class LoginFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer firstError;
    @Nullable
    private Integer lastError;
    @Nullable
    private Integer locationError;
    @Nullable
    private Integer bioError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer genderError;
    @Nullable
    private Integer imageError;

    private boolean isDataValid;

    LoginFormState(@Nullable Integer usernameError, @Nullable Integer firstError, @Nullable Integer lastError,
                   @Nullable Integer locationError, @Nullable Integer bioError, @Nullable Integer passwordError,
                   @Nullable Integer genderError, @Nullable Integer imageError) {
        this.usernameError = usernameError;
        this.firstError = firstError;
        this.lastError = lastError;
        this.locationError = locationError;
        this.bioError = bioError;
        this.passwordError = passwordError;
        this.genderError = genderError;
        this.imageError = imageError;
        this.isDataValid = false;
    }

    LoginFormState(boolean isDataValid) {
        this.usernameError = null;
        this.lastError = null;
        this.locationError = null;
        this.bioError = null;
        this.passwordError = null;
        this.genderError = null;
        this.imageError = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() {
        return usernameError;
    }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    boolean isDataValid() {
        return isDataValid;
    }

    @Nullable
    public Integer getFirstError() {
        return firstError;
    }

    @Nullable
    public Integer getLastError() {
        return lastError;
    }

    @Nullable
    public Integer getLocationError() {
        return locationError;
    }

    @Nullable
    public Integer getBioError() {
        return bioError;
    }

    @Nullable
    public Integer getGenderError() {
        return genderError;
    }

    @Nullable
    public Integer getImageError() {
        return imageError;
    }
}