package com.example.tinkofftest.dto.yandex;

public class YandexTranslatedWord {

    private String text;

    public YandexTranslatedWord() {
    }

    public YandexTranslatedWord(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
