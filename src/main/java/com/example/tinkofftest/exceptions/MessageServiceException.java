package com.example.tinkofftest.exceptions;

import org.springframework.http.HttpStatusCode;

public class MessageServiceException extends Exception {

    private final HttpStatusCode statusCode;

    public HttpStatusCode getStatusCode() {
        return statusCode;
    }

    public MessageServiceException(HttpStatusCode statusCode, String message, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

}
