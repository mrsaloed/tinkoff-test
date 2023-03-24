package com.example.tinkofftest.services;

import java.util.List;

public interface LogToDbService {
    void logSuccess(String inputData, String outputData, String parameters, List<String> translatedWords, String ip);
    void logFailure(String inputData, String outputData, String parameters, String ip);
}
