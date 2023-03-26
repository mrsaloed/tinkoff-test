package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.entities.RequestEntity;
import com.example.tinkofftest.exceptions.db.SaveToDatabaseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LogToDbServiceImplTest {

    @Autowired
    private LogToDbServiceImpl service;

    @Test
    void shouldPassNormally_logSuccess() {
        assertDoesNotThrow(() -> {
            RequestEntity request = getRequestEntity();
            String ip = "0:0:0:0:0:0:0:1";
            service.logSuccess(request, ip);
        });

    }

    @Test
    void shouldPassNormally_logFailure() {
        assertDoesNotThrow(() -> {
            RequestEntity request = getRequestEntity();
            String ip = "0:0:0:0:0:0:0:1";
            service.logFailure(request, ip);
        });
    }

    @Test
    void shouldThrowEx_logSuccess() {
        assertThrows(SaveToDatabaseException.class, () ->{
                    RequestEntity request = getBadRequest();
                    String ip = "0:0:0:0:0:0:0:1";
                    service.logSuccess(request, ip);
                }
                );
    }

    @Test
    void shouldThrowEx_lodFailure() {
        assertThrows(SaveToDatabaseException.class, ()->{
            RequestEntity request = getBadRequest();
            String ip = "0:0:0:0:0:0:0:1";
            service.logFailure(request, ip);
        });
    }

    private RequestEntity getBadRequest() {
        return new RequestEntity();
    }

    private RequestEntity getRequestEntity() {
        String inputData = "Hi, Tinkofj!";
        String outputData = "Привет, Тинькофф!";
        String parameters = "ru-en";
        List<String> translatedWords = List.of("Привет", "Тинькофф");
        return new RequestEntity(inputData, outputData, parameters, translatedWords);
    }
}