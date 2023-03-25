package com.example.tinkofftest.dto.yandex;

import java.util.List;

public class YandexMessageToTranslate {
    private List<String> texts;
    private String sourceLanguageCode;
    private String targetLanguageCode;
    private String folderId;

    public YandexMessageToTranslate() {
    }

    public YandexMessageToTranslate(List<String> texts, String sourceLanguageCode, String targetLanguageCode, String folderId) {
        this.texts = texts;
        this.sourceLanguageCode = sourceLanguageCode;
        this.targetLanguageCode = targetLanguageCode;
        this.folderId = folderId;
    }

    public String getFolderId() {
        return folderId;
    }

    public void setFolderId(String folderId) {
        this.folderId = folderId;
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
