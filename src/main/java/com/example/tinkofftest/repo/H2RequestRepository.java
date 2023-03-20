package com.example.tinkofftest.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class H2RequestRepository implements RequestsRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public H2RequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(UUID requestId, String requestMessage, String responseMessage, String params, String ip) {
        jdbcTemplate.update("INSERT INTO Requests values(?,?,?,?,?,?)",
                requestId,
                requestMessage,
                responseMessage,
                LocalDateTime.now().toLocalTime(),
                params,
                ip
        );
    }
}
