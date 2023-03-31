package com.example.tinkofftest.services;


import com.example.tinkofftest.exceptions.TranslateServiceException;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;


@RequestScope
public interface TranslateService {
    /**
     * @param message String with message to translate
     * @param parameters translate parameters
     * @return List of translated words
     * @throws TranslateServiceException
     */
    List<String> translate(String message, String parameters) throws TranslateServiceException;
}
