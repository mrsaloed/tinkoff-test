package com.example.tinkofftest.services;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.entities.RequestEntity;
import com.example.tinkofftest.exceptions.MessageServiceException;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@RequestScope
public interface MessageService {
    String getTranslateParameters();

    List<String> getTranslatedWords();

    String getMessage();

    TranslatedMessageBody translate(MessageToTranslateBody messageToTranslateBody) throws MessageServiceException;

    RequestEntity getRequestEntity();

}
