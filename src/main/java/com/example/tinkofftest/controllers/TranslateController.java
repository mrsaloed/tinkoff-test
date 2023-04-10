package com.example.tinkofftest.controllers;

import com.example.tinkofftest.dto.MessageToTranslateBody;
import com.example.tinkofftest.dto.TranslatedMessageBody;
import com.example.tinkofftest.entities.RequestEntity;
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


@RestController
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

        String ip = request.getRemoteAddr();
        RequestEntity requestEntity = messageService.getRequestEntity();

        logToDbService.logSuccess(requestEntity, ip);

        return new ResponseEntity<>(translatedMessage, HttpStatus.OK);
    }

}
