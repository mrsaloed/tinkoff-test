package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.dto.yandex.YandexMessageToTranslate;
import com.example.tinkofftest.dto.yandex.YandexTranslateError;
import com.example.tinkofftest.dto.yandex.YandexTranslatedMessage;
import com.example.tinkofftest.dto.yandex.YandexTranslatedWord;
import com.example.tinkofftest.exceptions.TranslateServiceException;
import com.example.tinkofftest.properties.YandexProperties;
import com.example.tinkofftest.services.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class YandexTranslateService implements TranslateService {

    private static final String REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS = "-";
    private static final String REGEX_FOR_SPLIT_WORDS_FROM_MESSAGE = "\\s";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private final YandexProperties yandexProperties;

    @Autowired
    public YandexTranslateService(YandexProperties yandexProperties) {
        this.yandexProperties = yandexProperties;
    }

    public List<String> translate(String message, String parameters) throws TranslateServiceException {
        String sourceLanguageCode = getSourceLanguageCodeFromParams(parameters);
        String targetLanguageCode = getTargetLanguageCodeFromParams(parameters);
        String[] wordsToTranslate = splitMessageToWords(message);
        YandexMessageToTranslate yandexMessageToTranslate = new YandexMessageToTranslate(
                wordsToTranslate,
                sourceLanguageCode,
                targetLanguageCode,
                yandexProperties.getFolderId()
        );
        YandexTranslatedMessage yandexTranslatedMessage = getTranslateFromYandex(yandexMessageToTranslate);
        return getTranslatedMessageFromYandexTranslatedMessage(yandexTranslatedMessage);
    }

    private String getSourceLanguageCodeFromParams(String parameters) {
        return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[0];
    }

    private String getTargetLanguageCodeFromParams(String parameters) {
        return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[1];
    }

    private String[] splitMessageToWords(String message) {
        return message.split(REGEX_FOR_SPLIT_WORDS_FROM_MESSAGE);
    }

    private YandexTranslatedMessage getTranslateFromYandex(YandexMessageToTranslate yandexMessageToTranslate)
            throws TranslateServiceException {
        RestTemplate yandexTranslate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION_HEADER_KEY, yandexProperties.getToken());
        try {
            HttpEntity<YandexMessageToTranslate> request = new HttpEntity<>(yandexMessageToTranslate, headers);
            return yandexTranslate.postForObject(yandexProperties.getApiPath(), request, YandexTranslatedMessage.class);
        } catch (HttpClientErrorException ex) {
            YandexTranslateError error = ex.getResponseBodyAs(YandexTranslateError.class);
            String errorMessage = error != null ? error.getMessage() : ex.getMessage();
            throw new TranslateServiceException(ex.getStatusCode(), errorMessage);
        }
    }

    private List<String> getTranslatedMessageFromYandexTranslatedMessage(YandexTranslatedMessage yandexTranslatedMessage) {
        return yandexTranslatedMessage.getTranslations().stream()
                .map(YandexTranslatedWord::getText)
                .collect(Collectors.toList());
    }

}
