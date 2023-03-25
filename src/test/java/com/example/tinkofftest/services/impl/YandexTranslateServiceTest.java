package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.exceptions.TranslateServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class YandexTranslateServiceTest {

    public static final String CORRECT_MESSAGE = "Hi Tinkoff!";
    public static final String CORRECT_PARAMS = "en-ru";
    public static final String INCORRECT_PARAMS = "1";
    public static final String EMPTY_MESSAGE = "";
    @Autowired
    private YandexTranslateService service;

    @Test
    void shouldThrowEx_translate() {

        Assertions.assertThrows(TranslateServiceException.class,
                () -> service.translate(CORRECT_MESSAGE, INCORRECT_PARAMS));

        Assertions.assertThrows(TranslateServiceException.class,
                () -> service.translate(EMPTY_MESSAGE, CORRECT_PARAMS));
    }

    @Test
    void shouldPassedNormally_translate() {
        List<String> actual = service.translate(CORRECT_MESSAGE, CORRECT_PARAMS);
        List<String> expected = getExpectedList();
        Assertions.assertLinesMatch(expected, actual);
    }

    private List<String> getExpectedList() {
        return List.of("Привет", "Тинькофф!");
    }

}
