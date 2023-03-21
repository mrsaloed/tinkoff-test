package com.example.tinkofftest.repo;

import java.util.List;
import java.util.UUID;

public interface WordRepository {
    void save(UUID requestId, List<String> words);
}
