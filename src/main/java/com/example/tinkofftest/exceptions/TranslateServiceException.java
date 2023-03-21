package com.example.tinkofftest.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;

public class TranslateServiceException extends HttpClientErrorException {

    public TranslateServiceException(HttpStatusCode statusCode) {
        super(statusCode);
    }

    public TranslateServiceException(HttpStatusCode statusCode, String statusText) {
        super(statusCode, statusText);
    }

    public TranslateServiceException(HttpStatusCode statusCode, String statusText, byte[] body, Charset responseCharset) {
        super(statusCode, statusText, body, responseCharset);
    }

    public TranslateServiceException(HttpStatusCode statusCode, String statusText, HttpHeaders headers, byte[] body, Charset responseCharset) {
        super(statusCode, statusText, headers, body, responseCharset);
    }

    public TranslateServiceException(String message, HttpStatusCode statusCode, String statusText, HttpHeaders headers, byte[] body, Charset responseCharset) {
        super(message, statusCode, statusText, headers, body, responseCharset);
    }
}
