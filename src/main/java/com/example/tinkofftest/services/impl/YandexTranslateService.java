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
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Service
public class YandexTranslateService implements TranslateService {

    private static final String REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS = "-";
    private static final String REGEX_FOR_SPLIT_WORDS_FROM_MESSAGE = "\\s+";
    private static final String AUTHORIZATION_HEADER_KEY = "Authorization";
    private static final String BAD_TRANSLATE_PARAMETERS_EXCEPTION_MESSAGE = "Bad translate parameters!";
    private static final int SOURCE_CODE_INDEX = 0;
    private static final int TARGET_CODE_INDEX = 1;
    private static final int MAX_COUNT_OF_LISTS = 10;
    private final YandexProperties yandexProperties;
    private final RestTemplate yandexTranslate;

    @Autowired
    public YandexTranslateService(YandexProperties yandexProperties, RestTemplate restTemplate) {
        this.yandexProperties = yandexProperties;
        this.yandexTranslate = restTemplate;
    }

    /**
     * @param message String with message to translate
     * @param parameters translate parameters
     * @return List of translated words
     * @throws TranslateServiceException can be thrown if translate parameters have wrong format
     * or if Yandex return an error
     */
    public List<String> translate(String message, String parameters) throws TranslateServiceException {
        String sourceLanguageCode = getSourceLanguageCodeFromParams(parameters);
        String targetLanguageCode = getTargetLanguageCodeFromParams(parameters);
        List<String> wordsToTranslate = splitMessageToWords(message);

        List<List<String>> partsOfInitialWordList = splitIntoParts(wordsToTranslate);
        List<YandexMessageToTranslate> messagesToTranslate =
                buildListOfMessagesToTranslate(partsOfInitialWordList, sourceLanguageCode, targetLanguageCode);

        List<YandexTranslatedMessage> translatedMessages = getAsyncTranslateFromYandex(messagesToTranslate);
        return getTranslatedWordsFromYandexTranslatedMessage(translatedMessages);
    }


    /**
     * @param parameters String parameters with source and target language code
     * @return String with only source language code
     */
    private String getSourceLanguageCodeFromParams(String parameters) {
        return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[SOURCE_CODE_INDEX];
    }


    /**
     * @param parameters String parameters with source and target language code
     * @return String with only target language code
     * @throws TranslateServiceException can be thrown if translate parameters have wrong format
     */
    private String getTargetLanguageCodeFromParams(String parameters) throws TranslateServiceException {
        try {
            return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[TARGET_CODE_INDEX];
        } catch (Exception e) {
            throw new TranslateServiceException(HttpStatus.BAD_REQUEST, BAD_TRANSLATE_PARAMETERS_EXCEPTION_MESSAGE);
        }
    }

    /**
     * @param message String original message
     * @return List of words from original message
     */
    private List<String> splitMessageToWords(String message) {
        return Arrays.stream(message.split(REGEX_FOR_SPLIT_WORDS_FROM_MESSAGE)).toList();
    }

    /**
     * @param wordsList original List of words
     * @return initial list of words split into several lists
     */
    private List<List<String>> splitIntoParts(List<String> wordsList) {
        int size = wordsList.size();
        int countOfLists = Math.min(size, MAX_COUNT_OF_LISTS);
        int partitionSize = size / countOfLists;
        int remainder = size % countOfLists;

        List<List<String>> parts = new ArrayList<>();
        int index = 0;
        for (int i = 0; i < countOfLists; i++) {
            int partitionLength = partitionSize + (i < remainder ? 1 : 0);
            parts.add(wordsList.subList(index, index + partitionLength));
            index += partitionLength;
        }
        return parts;
    }


    /**
     * @param listOfMessages List of lists with words to translate
     * @param sourceLanguageCode String of source language code
     * @param targetLanguageCode String of target language code
     * @return List of YandexMessageToTranslate
     */
    private List<YandexMessageToTranslate> buildListOfMessagesToTranslate(List<List<String>> listOfMessages,
                                                                          String sourceLanguageCode,
                                                                          String targetLanguageCode) {
        return listOfMessages.stream()
                .filter(Objects::nonNull)
                .filter(el -> !el.isEmpty())
                .map(el -> new YandexMessageToTranslate(el,
                        sourceLanguageCode, targetLanguageCode))
                .toList();
    }

    /**
     * @param messagesToTranslate List of YandexMessageToTranslate
     * @return List of YandexTranslatedMessage with translated message
     */
    private List<YandexTranslatedMessage> getAsyncTranslateFromYandex(List<YandexMessageToTranslate> messagesToTranslate) {
        List<Callable<YandexTranslatedMessage>> callables =
                messagesToTranslate.stream()
                        .map(el -> (Callable<YandexTranslatedMessage>) () -> getTranslateFromYandex(el))
                        .toList();

        List<FutureTask<YandexTranslatedMessage>> futureTasks = callables
                .stream()
                .map(FutureTask::new)
                .peek(el -> new Thread(el).start())
                .toList();

        List<YandexTranslatedMessage> result = new ArrayList<>();
        try {
            for (FutureTask<YandexTranslatedMessage> futureTask : futureTasks) {
                result.add(futureTask.get());
            }
        } catch (ExecutionException e) {
            throw (TranslateServiceException) e.getCause();
        } catch (InterruptedException e) {
            throw new TranslateServiceException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
        return result;
    }


    /**
     * @param yandexMessageToTranslate YandexMessageToTranslate to send it to yandex
     * @return YandexTranslatedMessage with translations from Yandex
     * @throws TranslateServiceException can be thrown if Yandex return an error
     */
    private YandexTranslatedMessage getTranslateFromYandex(YandexMessageToTranslate yandexMessageToTranslate)
            throws TranslateServiceException {
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

    /**
     * @param yandexTranslatedMessage to parse translated words from it
     * @return List of translated words
     */
    private List<String> getTranslatedWordsFromYandexTranslatedMessage(YandexTranslatedMessage yandexTranslatedMessage) {
        return yandexTranslatedMessage.getTranslations().stream()
                .map(YandexTranslatedWord::getText)
                .toList();
    }

    /**
     * @param translatedMessageList List of YandexTranslatedMessage
     * @return List of translated words
     */
    private List<String> getTranslatedWordsFromYandexTranslatedMessage(List<YandexTranslatedMessage> translatedMessageList) {
        List<String> result = new ArrayList<>();
        translatedMessageList.stream()
                .map(this::getTranslatedWordsFromYandexTranslatedMessage)
                .forEach(result::addAll);
        return result;
    }

}
