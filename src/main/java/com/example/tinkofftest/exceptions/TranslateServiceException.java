package com.example.tinkofftest.exceptions;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;


public class TranslateServiceException extends HttpClientErrorException {

    public TranslateServiceException(HttpStatusCode statusCode, String statusText) {
        super(statusCode, statusText);
    }

}
