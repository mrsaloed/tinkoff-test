package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.entities.RequestEntity;
import com.example.tinkofftest.exceptions.MessageServiceException;
import com.example.tinkofftest.exceptions.TranslateServiceException;
import com.example.tinkofftest.services.MessageService;
import com.example.tinkofftest.services.TranslateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@Service
@RequestScope
public class MessageServiceImpl implements MessageService {
    private static final String DELIMITER_FOR_CONVERT_WORDS_TO_MSG = " ";
    private static final String REGEX_FOR_ONLY_WORDS = "[^\\da-zA-Zа-яёА-ЯЁ ]";
    private static final String EMPTY_STRING = "";
    private String message;
    private List<String> translatedWords;
    private String translateParameters;

    private RequestEntity requestEntity;
    private final TranslateService translateService;

    @Autowired
    public MessageServiceImpl(TranslateService translateService) {
        this.translateService = translateService;
    }

    public String getTranslateParameters() {
        return translateParameters;
    }

    public List<String> getTranslatedWords() {
        return translatedWords.stream()
                .map(s -> s.replaceAll(REGEX_FOR_ONLY_WORDS, EMPTY_STRING))
                .filter(s -> s.length() > 0)
                .toList();
    }

    public String getMessage() {
        return message;
    }

    public TranslatedMessageBody translate(MessageToTranslateBody messageToTranslateBody) throws MessageServiceException {
        message = messageToTranslateBody.getMessage();
        translateParameters = messageToTranslateBody.getParameters();
        try {
            translatedWords = translateService.translate(message, translateParameters);
            String translatedMessage = getFromTranslatedWords();
            TranslatedMessageBody translatedMessageBody = new TranslatedMessageBody(translatedMessage);
            requestEntity = new RequestEntity(message, translatedMessage, translateParameters, translatedWords);
            return translatedMessageBody;
        } catch (TranslateServiceException ex) {
            requestEntity = new RequestEntity(message, ex.getMessage(), translateParameters);
            throw new MessageServiceException(ex.getStatusCode(), ex.getMessage(), ex);
        }
    }

    private String getFromTranslatedWords() {
        return String.join(DELIMITER_FOR_CONVERT_WORDS_TO_MSG, translatedWords);
    }

    public RequestEntity getRequestEntity() {
        return requestEntity;
    }
}
