package com.finova.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
public class RegisterRequest {



    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 6, max = 100, message = "Password must be at least 6 characters long")
    private String password;

    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;

    private String collegeName;

    private String studentStatus = "Undergraduate";

    private String currency = "INR";


    public RegisterRequest() {}

    public RegisterRequest(String fullName, String email, String password, String confirmPassword, String collegeName, String studentStatus, String currency) {
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.collegeName = collegeName;
        this.studentStatus = studentStatus;
        this.currency = currency;
    }

    public String getFullName() {
        return this.fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getCollegeName() {
        return this.collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getStudentStatus() {
        return this.studentStatus;
    }

    public void setStudentStatus(String studentStatus) {
        this.studentStatus = studentStatus;
    }

    public String getCurrency() {
        return this.currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public static RegisterRequestBuilder builder() {
        return new RegisterRequestBuilder();
    }

    public static class RegisterRequestBuilder {
        private String fullName;
        private String email;
        private String password;
        private String confirmPassword;
        private String collegeName;
        private String studentStatus;
        private String currency;

        public RegisterRequestBuilder() {}

        public RegisterRequestBuilder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        public RegisterRequestBuilder email(String email) {
            this.email = email;
            return this;
        }

        public RegisterRequestBuilder password(String password) {
            this.password = password;
            return this;
        }

        public RegisterRequestBuilder confirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
            return this;
        }

        public RegisterRequestBuilder collegeName(String collegeName) {
            this.collegeName = collegeName;
            return this;
        }

        public RegisterRequestBuilder studentStatus(String studentStatus) {
            this.studentStatus = studentStatus;
            return this;
        }

        public RegisterRequestBuilder currency(String currency) {
            this.currency = currency;
            return this;
        }

        public RegisterRequest build() {
            RegisterRequest instance = new RegisterRequest();
            instance.setFullName(this.fullName);
            instance.setEmail(this.email);
            instance.setPassword(this.password);
            instance.setConfirmPassword(this.confirmPassword);
            instance.setCollegeName(this.collegeName);
            instance.setStudentStatus(this.studentStatus);
            instance.setCurrency(this.currency);
            return instance;
        }
    }
}
