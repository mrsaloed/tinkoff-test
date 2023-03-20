package com.example.tinkofftest.dto.yandex;

import java.util.List;

public class YandexTranslatedMessage {
    private List<YandexTranslatedWord> translations;

    public List<YandexTranslatedWord> getTranslations() {
        return translations;
    }

    public void setTranslations(List<YandexTranslatedWord> translations) {
        this.translations = translations;
    }
}
