package com.example.tinkofftest.services;

import com.example.tinkofftest.entities.RequestEntity;

public interface LogToDbService {
    /**
     * @param requestEntity request which contains input and output data, parameters for translate
     * @param ip from which request was send
     */
    void logSuccess(RequestEntity requestEntity, String ip);

    void logFailure(RequestEntity requestEntity, String ip);
}
