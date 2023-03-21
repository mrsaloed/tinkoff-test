package com.example.tinkofftest.dto.yandex;

import java.io.Serializable;

public class YandexTranslateErrorDetail implements Serializable {
    private String type;
    private String requestId;

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
