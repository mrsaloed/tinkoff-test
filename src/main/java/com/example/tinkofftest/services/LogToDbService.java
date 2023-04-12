package com.example.tinkofftest.services;

import com.example.tinkofftest.entities.RequestEntity;

public interface LogToDbService {
    /**
     * Logging a successful request into DB. It's inserting information into Requests and Words tables.
     * @param requestEntity request which contains input and output data, parameters for translate
     * @param ip IP address from which request was sent.
     */
    void logSuccess(RequestEntity requestEntity, String ip);

    /**
     * Logging a failure request into DB. It's inserting information only into Requests table.
     * @param requestEntity request which contains input and output data, parameters for translate.
     * @param ip IP address from which request was sent.
     */
    void logFailure(RequestEntity requestEntity, String ip);
}
