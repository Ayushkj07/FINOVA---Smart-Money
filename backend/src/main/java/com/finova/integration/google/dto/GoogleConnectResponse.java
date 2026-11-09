package com.finova.integration.google.dto;

public class GoogleConnectResponse {
    private String authUrl;
    private String service;
    private String state;
    private String scope;

    public GoogleConnectResponse() {}

    public GoogleConnectResponse(String authUrl, String state, String service) {
        this.authUrl = authUrl;
        this.state = state;
        this.service = service;
    }

    public GoogleConnectResponse(String authUrl, String state, String service, String scope) {
        this.authUrl = authUrl;
        this.state = state;
        this.service = service;
        this.scope = scope;
    }

    public String getAuthUrl() { return authUrl; }
    public void setAuthUrl(String authUrl) { this.authUrl = authUrl; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getScope() { return scope; }
    public void setScope(String scope) { this.scope = scope; }
}
