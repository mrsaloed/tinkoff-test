package com.example.tinkofftest.dto.yandex;

import java.util.List;

public class YandexTranslateError {
    private int code;
    private String message;

    private List<YandexTranslateErrorDetail> details;

    public List<YandexTranslateErrorDetail> getDetails() {
        return details;
    }

    public void setDetails(List<YandexTranslateErrorDetail> details) {
        this.details = details;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
