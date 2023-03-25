package com.example.tinkofftest.dto;

import java.util.List;

public class TranslatedMessageBody {

    private String message;

    public TranslatedMessageBody() {
    }

    public TranslatedMessageBody(String message) {
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(List<String> message) {
        this.message = String.join(" ", message);
    }
}
