package com.example.tinkofftest.services;

import com.example.tinkofftest.entities.RequestEntity;

public interface LogToDbService {
    void logSuccess(RequestEntity requestEntity, String ip);

    void logFailure(RequestEntity requestEntity, String ip);
}
