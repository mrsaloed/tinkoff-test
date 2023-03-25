package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.exceptions.MessageServiceException;
import com.example.tinkofftest.exceptions.TranslateServiceException;
import com.example.tinkofftest.services.TranslateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

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
        messageToTranslateBody = getCorrectMessage();
        messageService.translate(messageToTranslateBody);
        Mockito.doThrow(TranslateServiceException.class)
                .when(translateService)
                .translate(EMPTY_MESSAGE, CORRECT_PARAMS);
        Mockito.doThrow(TranslateServiceException.class)
                .when(translateService)
                .translate(CORRECT_MESSAGE, INCORRECT_PARAMS);
    }

    @Test
    void shouldPassNormally_getTranslateParameters() {
        String actual = messageService.getTranslateParameters();
        assertEquals(CORRECT_PARAMS, actual);
    }

    @Test
    void shouldThrowEx_getTranslateParameters() {
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
    void shouldPassNormally_getMessage() {
        String actual = messageService.getMessage();
        assertEquals(CORRECT_MESSAGE, actual);
    }

    @Test
    void shouldThrowEx_getMessage() {
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
    void translate() {
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
        assertEquals(CORRECT_MESSAGE, messageService.getMessage());
        assertEquals(CORRECT_PARAMS, messageService.getTranslateParameters());
    }

    @Test
    void getFromTranslatedWords() {
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
    }

    @Test
    void getRequestEntity() {
        Mockito.verify(translateService)
                .translate(CORRECT_MESSAGE, CORRECT_PARAMS);
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