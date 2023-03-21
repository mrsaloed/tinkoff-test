package com.example.tinkofftest.repo.impl;

import com.example.tinkofftest.repo.RequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class H2RequestRepository implements RequestsRepository {

    private static final String SQL_INSERT = "INSERT INTO Requests values(?,?,?,?,?,?)";
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public H2RequestRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(UUID requestId, String requestMessage, String responseMessage, String params, String ip) {
        jdbcTemplate.update(SQL_INSERT,
                requestId,
                requestMessage,
                responseMessage,
                LocalDateTime.now().toLocalTime(),
                params,
                ip
        );
    }
}
