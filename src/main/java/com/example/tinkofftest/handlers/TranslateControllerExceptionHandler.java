package com.example.tinkofftest.handlers;

import com.example.tinkofftest.dto.ErrorBody;
import com.example.tinkofftest.entities.RequestEntity;
import com.example.tinkofftest.exceptions.MessageServiceException;
import com.example.tinkofftest.exceptions.db.SaveToDatabaseException;
import com.example.tinkofftest.services.LogToDbService;
import com.example.tinkofftest.services.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
class TranslateControllerExceptionHandler {

    private final MessageService messageService;
    private final LogToDbService logToDbService;

    @Autowired
    public TranslateControllerExceptionHandler(MessageService messageService, LogToDbService logToDbService) {
        this.logToDbService = logToDbService;
        this.messageService = messageService;
    }

    @ExceptionHandler(MessageServiceException.class)
    public ResponseEntity<ErrorBody> messageServiceExceptionHandler(MessageServiceException ex,
                                                                    HttpServletRequest request) {
        ErrorBody errorBody = new ErrorBody(ex.getMessage(), ex.getStatusCode());

        String ip = request.getRemoteAddr();
        RequestEntity requestEntity = messageService.getRequestEntity();

        logToDbService.logFailure(requestEntity, ip);

        return new ResponseEntity<>(errorBody, ex.getStatusCode());
    }

    @ExceptionHandler(SaveToDatabaseException.class)
    public ResponseEntity<ErrorBody> databaseExceptionHandler(SaveToDatabaseException ex,
                                                              HttpServletRequest request) {
        ErrorBody errorBody = new ErrorBody(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);

        String ip = request.getRemoteAddr();
        RequestEntity requestEntity = messageService.getRequestEntity();

        logToDbService.logFailure(requestEntity, ip);

        return new ResponseEntity<>(errorBody, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
