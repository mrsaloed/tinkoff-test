package com.example.tinkofftest.dto;

import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.List;

@Component
public class TranslatedMessageBody implements Serializable {

    private String message;

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
