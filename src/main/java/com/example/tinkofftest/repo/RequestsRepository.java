package com.example.tinkofftest.repo;

import java.util.UUID;

public interface RequestsRepository {
    void save(UUID requestId, String requestMessage, String responseMessage, String params, String ip);
}
