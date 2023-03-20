package com.example.tinkofftest.controllers;


import com.example.tinkofftest.dto.ErrorBody;
import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.exceptions.TranslateServiceException;
import com.example.tinkofftest.repo.RequestsRepository;
import com.example.tinkofftest.repo.WordRepository;
import com.example.tinkofftest.services.TranslateService;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.UUID;


@RestController
@RequestScope
public class TranslateController {

    private final TranslateService translateService;
    private final RequestsRepository requestsRepository;
    private final WordRepository wordRepository;

    @Autowired
    public TranslateController(TranslateService translateService, RequestsRepository requestsRepository, WordRepository wordRepository) {
        this.translateService = translateService;
        this.requestsRepository = requestsRepository;
        this.wordRepository = wordRepository;
    }

    @PostMapping(value = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public ResponseEntity<?> translate(@RequestBody MessageToTranslateBody messageToTranslateBody,
                                       HttpServletRequest request) {
        UUID requestId = UUID.randomUUID();
        String messageToTranslate = messageToTranslateBody.getMessage();
        String parametersToTranslate = messageToTranslateBody.getParameters();
        List<String> translatedMessage = null;//todo add to Object getWords() method
        ResponseEntity<?> response;
        String responseToDB;
        try {
            translatedMessage = translateService.translate(messageToTranslate, parametersToTranslate);
            TranslatedMessageBody translatedMessageBody = new TranslatedMessageBody();
            translatedMessageBody.setMessage(translatedMessage);
            response = new ResponseEntity<>(translatedMessageBody, HttpStatus.OK);
            responseToDB = translatedMessageBody.getMessage();
        } catch (TranslateServiceException ex) {
            ErrorBody errorBody = new ErrorBody(ex.getMessage());
            response = new ResponseEntity<>(errorBody, ex.getStatusCode());
            responseToDB = errorBody.getError();
        }
        requestsRepository.save(requestId, messageToTranslate, responseToDB, parametersToTranslate, request.getRemoteAddr());
        if (translatedMessage != null) {
            wordRepository.save(requestId, translatedMessage);
        }
        return response;
    }

}
