package com.example.tinkofftest.repo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class H2WordRepository implements WordRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public H2WordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void save(UUID requestId, List<String> word) {
        jdbcTemplate.batchUpdate("INSERT INTO Words VALUES (?,?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement ps, int i) throws SQLException {
                        ps.setString(1, String.valueOf(requestId));
                        ps.setString(2, word.get(i));
                    }

                    @Override
                    public int getBatchSize() {
                        return word.size();
                    }
                });
    }
}
