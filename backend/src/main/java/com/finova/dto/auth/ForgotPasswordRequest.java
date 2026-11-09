package com.finova.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
public class ForgotPasswordRequest {


    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;


    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static ForgotPasswordRequestBuilder builder() {
        return new ForgotPasswordRequestBuilder();
    }

    public static class ForgotPasswordRequestBuilder {
        private String email;

        public ForgotPasswordRequestBuilder() {}

        public ForgotPasswordRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public ForgotPasswordRequest build() {
            ForgotPasswordRequest instance = new ForgotPasswordRequest();
            instance.setEmail(this.email);
            return instance;
        }
    }
}
