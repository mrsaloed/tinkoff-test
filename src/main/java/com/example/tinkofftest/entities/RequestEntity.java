package com.example.tinkofftest.entities;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class RequestEntity {
    private final UUID id;
    private String inputData;
    private String outputData;
    private String parameters;
    private List<String> translatedWords;
    private final Time requestTime;

    public RequestEntity() {
        this.id = UUID.randomUUID();
        this.requestTime = Time.valueOf(LocalDateTime.now().toLocalTime());
    }

    public RequestEntity(String inputData, String outputData, String parameters, List<String> translatedWords) {
        this();
        this.inputData = inputData;
        this.outputData = outputData;
        this.parameters = parameters;
        this.translatedWords = translatedWords;
    }

    public RequestEntity(String inputData, String outputData, String parameters) {
        this();
        this.inputData = inputData;
        this.outputData = outputData;
        this.parameters = parameters;
    }

    public Time getRequestTime() {
        return requestTime;
    }

    public UUID getId() {
        return id;
    }

    public String getInputData() {
        return inputData;
    }

    public void setInputData(String inputData) {
        this.inputData = inputData;
    }

    public String getOutputData() {
        return outputData;
    }

    public void setOutputData(String outputData) {
        this.outputData = outputData;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public List<String> getTranslatedWords() {
        return translatedWords;
    }

    public void setTranslatedWords(List<String> translatedWords) {
        this.translatedWords = translatedWords;
    }
}
