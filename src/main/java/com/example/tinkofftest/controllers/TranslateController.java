package com.example.tinkofftest.controllers;

import com.example.tinkofftest.dto.ErrorBody;
import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.exceptions.MessageServiceException;
import com.example.tinkofftest.exceptions.db.SaveToDatabaseException;
import com.example.tinkofftest.services.LogToDbService;
import com.example.tinkofftest.services.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
    public ResponseEntity<TranslatedMessageBody> translate(@RequestBody MessageToTranslateBody messageToTranslateBody,
                                       HttpServletRequest request) {
        TranslatedMessageBody translatedMessage = messageService.translate(messageToTranslateBody);

        String inputData = messageService.getMessage();
        String parameters = messageService.getTranslateParameters();
        String ip = request.getRemoteAddr();
        String outputData = translatedMessage.getMessage();
        List<String> words = messageService.getTranslatedWords();

        logToDbService.logSuccess(inputData, outputData, parameters, words, ip);

        return new ResponseEntity<>(translatedMessage, HttpStatus.OK);
    }

    @ExceptionHandler(MessageServiceException.class)
    public ResponseEntity<ErrorBody> messageServiceExceptionHandler(MessageServiceException ex,
                                                                    HttpServletRequest request) {
        ErrorBody errorBody = new ErrorBody(ex.getMessage(), ex.getStatusCode());

        String inputData = messageService.getMessage();
        String parameters = messageService.getTranslateParameters();
        String ip = request.getRemoteAddr();
        String outputData = ex.getMessage();

        logToDbService.logFailure(inputData, outputData, parameters, ip);

        return new ResponseEntity<>(errorBody, ex.getStatusCode());
    }

    @ExceptionHandler(SaveToDatabaseException.class)
    public ResponseEntity<ErrorBody> databaseExceptionHandler(SaveToDatabaseException ex,
                                                   HttpServletRequest request) {
        ErrorBody errorBody = new ErrorBody(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        String inputData = messageService.getMessage();
        String parameters = messageService.getTranslateParameters();
        String ip = request.getRemoteAddr();
        String outputData = ex.getMessage();

        logToDbService.logFailure(inputData, outputData, parameters, ip);

        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
