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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class YandexTranslateService implements TranslateService {

    private static final String REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS = "-";
    private static final String REGEX_FOR_SPLIT_WORDS_FROM_MESSAGE = "\\s+";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BAD_TRANSLATE_PARAMETERS_EXCEPTION_MESSAGE = "Bad translate parameters!";
    public static final int COUNT_OF_THREADS = 10;
    private final YandexProperties yandexProperties;

    @Autowired
    public YandexTranslateService(YandexProperties yandexProperties) {
        this.yandexProperties = yandexProperties;
    }

    @Async
    public List<String> translate(String message, String parameters) throws TranslateServiceException {
        String sourceLanguageCode = getSourceLanguageCodeFromParams(parameters);
        String targetLanguageCode = getTargetLanguageCodeFromParams(parameters);
        List<String> wordsToTranslate = splitMessageToWords(message);

        List<List<String>> parts = getTenSubList(wordsToTranslate);

        List<CompletableFuture<YandexTranslatedMessage>> completableFutures = new ArrayList<>();
        for (int i = 0; i < COUNT_OF_THREADS; i++) {
            YandexMessageToTranslate yandexMessageToTranslate = new YandexMessageToTranslate(
                    parts.get(i),
                    sourceLanguageCode,
                    targetLanguageCode,
                    yandexProperties.getFolderId()
            );
            completableFutures.add(getTranslateFromYandex(yandexMessageToTranslate));
        }

        List<YandexTranslatedMessage> translatedWords = completableFutures.stream()
                .map(CompletableFuture::join)
                .toList();

        List<String> list = new ArrayList<>();
        translatedWords.stream()
                .map(this::getTranslatedMessageFromYandexTranslatedMessage).
                forEach(list::addAll);

        return list;
    }

    @Async
    public CompletableFuture<YandexTranslatedMessage> getTranslateFromYandex(YandexMessageToTranslate yandexMessageToTranslate)
            throws TranslateServiceException {
        RestTemplate yandexTranslate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(AUTHORIZATION_HEADER_KEY, yandexProperties.getToken());
        try {
            HttpEntity<YandexMessageToTranslate> request = new HttpEntity<>(yandexMessageToTranslate, headers);
            return CompletableFuture.completedFuture(yandexTranslate.postForObject(yandexProperties.getApiPath(), request, YandexTranslatedMessage.class));
        } catch (HttpClientErrorException ex) {
            YandexTranslateError error = ex.getResponseBodyAs(YandexTranslateError.class);
            String errorMessage = error != null ? error.getMessage() : ex.getMessage();
            throw new TranslateServiceException(ex.getStatusCode(), errorMessage);
        }
    }

    private String getSourceLanguageCodeFromParams(String parameters) throws TranslateServiceException {
        try {
            return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[0];
        } catch (Exception e) {
            throw new TranslateServiceException(HttpStatus.BAD_REQUEST, BAD_TRANSLATE_PARAMETERS_EXCEPTION_MESSAGE);
        }
    }

    private String getTargetLanguageCodeFromParams(String parameters) throws TranslateServiceException {
        try {
            return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[1];
        } catch (Exception e) {
            throw new TranslateServiceException(HttpStatus.BAD_REQUEST, BAD_TRANSLATE_PARAMETERS_EXCEPTION_MESSAGE);
        }
    }

    private List<String> splitMessageToWords(String message) {
        return Arrays.stream(message.split(REGEX_FOR_SPLIT_WORDS_FROM_MESSAGE)).toList();
    }

    private List<String> getTranslatedMessageFromYandexTranslatedMessage(YandexTranslatedMessage yandexTranslatedMessage) {
        return yandexTranslatedMessage.getTranslations().stream()
                .map(YandexTranslatedWord::getText)
                .collect(Collectors.toList());
    }

    private List<List<String>> getTenSubList(List<String> array) {
        int size = array.size();
        int partitionSize = size/ COUNT_OF_THREADS;
        int remainder = size % COUNT_OF_THREADS;

        List<List<String>> parts = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < COUNT_OF_THREADS; i++) {
            int partitionLength = partitionSize + (i < remainder ? 1 : 0);
            parts.add(array.subList(index, index + partitionLength));
            index += partitionLength;
        }
        return parts;
    }

}
