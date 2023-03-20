package com.example.tinkofftest.dto;

public class ErrorBody {
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public ErrorBody(String error) {
        this.error = error;
    }
}
