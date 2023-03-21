package com.example.tinkofftest.controllers;


import com.example.tinkofftest.dto.ErrorBody;
import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.exceptions.MessageServiceException;
import com.example.tinkofftest.services.LogToDbService;
import com.example.tinkofftest.services.MessageService;
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


@RestController
@RequestScope
public class TranslateController {

    private final MessageService messageService;
    private final LogToDbService logToDbService;

    @Autowired
    public TranslateController(MessageService messageService, LogToDbService logToDbService) {
        this.logToDbService = logToDbService;
        this.messageService = messageService;
    }

    @PostMapping(value = "/",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_HTML_VALUE})
    public ResponseEntity<?> translate(@RequestBody MessageToTranslateBody messageToTranslateBody,
                                       HttpServletRequest request) {
        TranslatedMessageBody translatedMessage = null;
        ErrorBody errorBody = null;
        ResponseEntity<?> response;
        try {
            translatedMessage = messageService.translate(messageToTranslateBody);
            response = new ResponseEntity<>(translatedMessage, HttpStatus.OK);
        } catch (MessageServiceException ex) {
            errorBody = new ErrorBody(ex.getMessage(), ex.getStatusCode());
            response = new ResponseEntity<>(errorBody, ex.getStatusCode());
        } finally {
            String inputData = messageService.getMessage();
            String parameters = messageService.getTranslateParameters();
            String ip = request.getRemoteAddr();
            String outputData;
            List<String> words;
            if (translatedMessage != null) {
                outputData = translatedMessage.getMessage();
                words = messageService.getTranslatedWords();
            } else {
                outputData = errorBody.getError();
                words = null;
            }
            logToDbService.log(inputData, outputData, parameters, words, ip);
        }
        return response;
    }

}
