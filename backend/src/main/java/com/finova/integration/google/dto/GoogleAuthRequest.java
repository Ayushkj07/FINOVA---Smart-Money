package com.finova.integration.google.dto;

public class GoogleAuthRequest {
    private String idToken;
    private String code;
    private String email;
    private String fullName;
    private String sub;
    private String picture;
    private String collegeName;
    private String currency;
    private String redirectUri;
    private String state;

    public GoogleAuthRequest() {}

    public GoogleAuthRequest(String idToken, String code, String email, String fullName, String sub, String picture, String collegeName, String currency, String redirectUri, String state) {
        this.idToken = idToken;
        this.code = code;
        this.email = email;
        this.fullName = fullName;
        this.sub = sub;
        this.picture = picture;
        this.collegeName = collegeName;
        this.currency = currency;
        this.redirectUri = redirectUri;
        this.state = state;
    }

    public String getIdToken() { return idToken; }
    public void setIdToken(String idToken) { this.idToken = idToken; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getSub() { return sub; }
    public void setSub(String sub) { this.sub = sub; }

    public String getPicture() { return picture; }
    public void setPicture(String picture) { this.picture = picture; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getRedirectUri() { return redirectUri; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
}
