package com.example.tinkofftest.services;

import java.util.List;

public interface LogToDbService {
    void log(String inputData, String outputData, String parameters, List<String> translatedWords, String ip);
}
