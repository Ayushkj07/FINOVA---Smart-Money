package com.finova.integration.google.dto;

public class GoogleAuthRequest {
    private String idToken;
    private String code;
    private String collegeName;
    private String currency;

    public GoogleAuthRequest() {}

    public GoogleAuthRequest(String idToken, String code, String collegeName, String currency) {
        this.idToken = idToken;
        this.code = code;
        this.collegeName = collegeName;
        this.currency = currency;
    }

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
