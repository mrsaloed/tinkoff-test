package com.example.tinkofftest.dto.yandex;

import java.util.List;

public class YandexMessageToTranslate {
    private List<String> texts;
    private String sourceLanguageCode;
    private String targetLanguageCode;

    public YandexMessageToTranslate() {
    }

    public YandexMessageToTranslate(List<String> texts, String sourceLanguageCode, String targetLanguageCode) {
        this.texts = texts;
        this.sourceLanguageCode = sourceLanguageCode;
        this.targetLanguageCode = targetLanguageCode;
    }

    public String getTargetLanguageCode() {
        return targetLanguageCode;
    }

    public void setTargetLanguageCode(String targetLanguageCode) {
        this.targetLanguageCode = targetLanguageCode;
    }

    public String getSourceLanguageCode() {
        return sourceLanguageCode;
    }

    public void setSourceLanguageCode(String sourceLanguageCode) {
        this.sourceLanguageCode = sourceLanguageCode;
    }

    public List<String> getTexts() {
        return texts;
    }

    public void setTexts(List<String> texts) {
        this.texts = texts;
    }

}
