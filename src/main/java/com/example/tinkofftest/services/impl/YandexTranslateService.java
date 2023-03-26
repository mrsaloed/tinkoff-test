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
import java.util.stream.Collectors;

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

    private String getSourceLanguageCodeFromParams(String parameters) {
        return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[SOURCE_CODE_INDEX];
    }

    private String getTargetLanguageCodeFromParams(String parameters) throws TranslateServiceException {
        try {
            return parameters.split(REGEX_FOR_SPLIT_LANGUAGE_PARAMETERS)[TARGET_CODE_INDEX];
        } catch (Exception e) {
            throw new TranslateServiceException(HttpStatus.BAD_REQUEST, BAD_TRANSLATE_PARAMETERS_EXCEPTION_MESSAGE);
        }
    }

    private List<String> splitMessageToWords(String message) {
        return Arrays.stream(message.split(REGEX_FOR_SPLIT_WORDS_FROM_MESSAGE)).toList();
    }

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

    private List<YandexMessageToTranslate> buildListOfMessagesToTranslate(List<List<String>> listOfMessages,
                                                                          String sourceLanguageCode,
                                                                          String targetLanguageCode) {
        return listOfMessages.stream()
                .filter(Objects::nonNull)
                .filter(el -> !el.isEmpty())
                .map(el -> new YandexMessageToTranslate(el,
                        sourceLanguageCode, targetLanguageCode))
                .collect(Collectors.toList());
    }

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

    private List<String> getTranslatedWordsFromYandexTranslatedMessage(YandexTranslatedMessage yandexTranslatedMessage) {
        return yandexTranslatedMessage.getTranslations().stream()
                .map(YandexTranslatedWord::getText)
                .collect(Collectors.toList());
    }

    private List<String> getTranslatedWordsFromYandexTranslatedMessage(List<YandexTranslatedMessage> translatedMessageList) {
        List<String> result = new ArrayList<>();
        translatedMessageList.stream()
                .map(this::getTranslatedWordsFromYandexTranslatedMessage)
                .forEach(result::addAll);
        return result;
    }

}
