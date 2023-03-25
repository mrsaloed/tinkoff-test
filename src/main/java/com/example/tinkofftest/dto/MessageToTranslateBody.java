package com.example.tinkofftest.dto;

public class MessageToTranslateBody {

    private String parameters;
    private String message;

    public MessageToTranslateBody() {
    }

    public MessageToTranslateBody(String parameters, String message) {
        this.parameters = parameters;
        this.message = message;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
