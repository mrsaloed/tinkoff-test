package com.example.tinkofftest.dto;

import org.springframework.http.HttpStatusCode;


public class ErrorBody {

    private int statusCode;
    private String error;

    public ErrorBody() {
    }

    public ErrorBody(int statusCode, String error) {
        this.statusCode = statusCode;
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatusCode statusCode) {
        this.statusCode = statusCode.value();
    }


    public ErrorBody(String error, HttpStatusCode statusCode) {
        this.error = error;
        this.statusCode = statusCode.value();
    }
}
