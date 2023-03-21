package com.example.tinkofftest.services;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.exceptions.MessageServiceException;

import java.util.List;

public interface MessageService {
    String getTranslateParameters();

    List<String> getTranslatedWords();

    String getMessage();

    TranslatedMessageBody translate(MessageToTranslateBody messageToTranslateBody) throws MessageServiceException;

}
