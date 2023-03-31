package com.example.tinkofftest.services;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.entities.RequestEntity;
import com.example.tinkofftest.exceptions.MessageServiceException;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;

@RequestScope
public interface MessageService {

    /**
     * @return String with parameters for translate
     */
    String getTranslateParameters();

    /**
     * @return List of translated words
     */
    List<String> getTranslatedWords();

    /**
     * @return incoming message to translate
     */
    String getMessage();

    /**
     * @param messageToTranslateBody body which was sended with request from controller
     * @return response body with translated message
     * @throws MessageServiceException if some problems with translateService
     */
    TranslatedMessageBody translate(MessageToTranslateBody messageToTranslateBody) throws MessageServiceException;

    /**
     * @return RequestEntity which would be logged into db
     */
    RequestEntity getRequestEntity();

}
