package com.finova.integration.google.dto;

public class GoogleCallbackRequest {
    private String code;
    private String state;
    private String service;
    private String error;

    public GoogleCallbackRequest() {}

    public GoogleCallbackRequest(String code, String state, String service, String error) {
        this.code = code;
        this.state = state;
        this.service = service;
        this.error = error;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getState() { return state; }
    public void setState(String state) { this.state = state; }

    public String getService() { return service; }
    public void setService(String service) { this.service = service; }

    public String getServiceName() { return service; }
    public void setServiceName(String serviceName) { this.service = serviceName; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
