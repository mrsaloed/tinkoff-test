package com.example.tinkofftest.dto.yandex;

public class YandexTranslateErrorDetail {
    private String type;
    private String requestId;

    public YandexTranslateErrorDetail() {
    }

    public YandexTranslateErrorDetail(String type, String requestId) {
        this.type = type;
        this.requestId = requestId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
