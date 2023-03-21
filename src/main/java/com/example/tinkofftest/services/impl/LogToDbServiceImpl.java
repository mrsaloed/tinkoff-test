package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.repo.RequestsRepository;
import com.example.tinkofftest.repo.WordRepository;
import com.example.tinkofftest.services.LogToDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class LogToDbServiceImpl implements LogToDbService {

    private final RequestsRepository requestsRepository;
    private final WordRepository wordRepository;

    @Autowired
    public LogToDbServiceImpl(RequestsRepository requestsRepository, WordRepository wordRepository) {
        this.requestsRepository = requestsRepository;
        this.wordRepository = wordRepository;
    }

    public void log(String inputData,
                    String outputData,
                    String parameters,
                    List<String> translatedWords,
                    String ip) {
        UUID requestId = UUID.randomUUID();
        requestsRepository.save(requestId, inputData, outputData, parameters, ip);
        if (translatedWords != null) {
            wordRepository.save(requestId, translatedWords);
        }
    }
}
