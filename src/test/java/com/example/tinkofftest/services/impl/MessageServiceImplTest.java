package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.entities.RequestEntity;
import com.example.tinkofftest.exceptions.MessageServiceException;
import com.example.tinkofftest.exceptions.TranslateServiceException;
import com.example.tinkofftest.services.TranslateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MessageServiceImplTest {

    private static final String CORRECT_MESSAGE = "Hi, Tinkoff!";
    private static final String CORRECT_PARAMS = "en-ru";
    private static final String INCORRECT_PARAMS = "params";
    private static final String EMPTY_MESSAGE = "";
    @Autowired
    private MessageServiceImpl messageService;

    @MockBean
    private TranslateService translateService;

    private MessageToTranslateBody messageToTranslateBody;

    @BeforeEach
    void init() {
        Mockito.doThrow(TranslateServiceException.class)
                .when(translateService)
                .translate(EMPTY_MESSAGE, CORRECT_PARAMS);
        Mockito.doThrow(TranslateServiceException.class)
                .when(translateService)
                .translate(CORRECT_MESSAGE, INCORRECT_PARAMS);
        Mockito.doReturn(getExpectedMessageFromTranslateService())
                .when(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
    }

    @Test
    void shouldPassNormally_getTranslateParameters() {
        messageToTranslateBody = getCorrectMessage();
        messageService.translate(messageToTranslateBody);
        String actual = messageService.getTranslateParameters();
        assertEquals(CORRECT_PARAMS, actual);
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
    }

    @Test
    void shouldThrowEx_getTranslateParameters() {
        assertThrows(MessageServiceException.class, () -> {
            messageService = new MessageServiceImpl(translateService);
            messageService.getTranslateParameters();
        });
    }

    @Test
    void shouldPassNormally_getMessage() {
        messageToTranslateBody = getCorrectMessage();
        messageService.translate(messageToTranslateBody);
        String actual = messageService.getMessage();
        assertEquals(CORRECT_MESSAGE, actual);
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
    }

    @Test
    void shouldThrowEx_getMessage() {
        assertThrows(MessageServiceException.class, () -> {
            messageService = new MessageServiceImpl(translateService);
            messageService.getMessage();
        });
    }

    @Test
    void shouldPassNormally_translate() {
        messageToTranslateBody = getCorrectMessage();
        messageService.translate(messageToTranslateBody);
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
        assertEquals(CORRECT_MESSAGE, messageService.getMessage());
        assertEquals(CORRECT_PARAMS, messageService.getTranslateParameters());
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
    }

    @Test
    void shouldThrowEx_translate() {
        assertThrows(MessageServiceException.class, () -> {
            messageToTranslateBody = getIncorrectMessage();
            messageService.translate(messageToTranslateBody);
        });
        assertThrows(MessageServiceException.class, () -> {
            messageToTranslateBody = getIncorrectParams();
            messageService.translate(messageToTranslateBody);
        });
    }

    @Test
    void shouldPassNormally_getRequestEntity() {
        messageToTranslateBody = getCorrectMessage();
        messageService.translate(messageToTranslateBody);
        RequestEntity actual = messageService.getRequestEntity();
        RequestEntity expected = new RequestEntity(CORRECT_MESSAGE, "Привет, Тинькофф!", CORRECT_PARAMS);
        assertEquals(expected.getInputData(), actual.getInputData());
        assertEquals(expected.getOutputData(), actual.getOutputData());
        assertEquals(expected.getParameters(), actual.getParameters());
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
    }

    @Test
    void shouldThrowEx_getRequestEntity() {
        messageService = new MessageServiceImpl(translateService);
        assertThrows(MessageServiceException.class, () -> messageService.getRequestEntity());
    }

    @Test
    void shouldPassNormally_getTranslatedWords() {
        messageToTranslateBody = getCorrectMessage();
        messageService.translate(messageToTranslateBody);
        List<String> expected = getExpectedWordsList();
        List<String> actual = messageService.getTranslatedWords();
        assertLinesMatch(expected, actual);
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
    }

    @Test
    void shouldThrowEx_getTranslatedWords() {
        assertThrows(MessageServiceException.class, () -> {
            messageService = new MessageServiceImpl(translateService);
            messageService.getTranslatedWords();
        });
    }


    private List<String> getExpectedMessageFromTranslateService() {
        return List.of("Привет,", "Тинькофф!");
    }

    private List<String> getExpectedWordsList() {
        return List.of("Привет", "Тинькофф");
    }

    private MessageToTranslateBody getCorrectMessage() {
        MessageToTranslateBody message = new MessageToTranslateBody();
        message.setMessage(CORRECT_MESSAGE);
        message.setParameters(CORRECT_PARAMS);
        return message;
    }

    private MessageToTranslateBody getIncorrectParams() {
        MessageToTranslateBody message = new MessageToTranslateBody();
        message.setMessage(CORRECT_MESSAGE);
        message.setParameters(INCORRECT_PARAMS);
        return message;
    }

    private MessageToTranslateBody getIncorrectMessage() {
        MessageToTranslateBody message = new MessageToTranslateBody();
        message.setMessage(EMPTY_MESSAGE);
        message.setParameters(CORRECT_PARAMS);
        return message;
    }
}