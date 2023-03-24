package com.example.tinkofftest.repo.impl;

import com.example.tinkofftest.exceptions.WordRepositoryException;
import com.example.tinkofftest.properties.H2Properties;
import com.example.tinkofftest.repo.WordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

@Repository
public class H2WordRepository implements WordRepository {

    private static final String SQL_INSERT = "INSERT INTO Words VALUES (?,?)";
    private static final int REQUEST_ID_PARAMETER_INDEX = 1;
    private static final int WORD_PARAMETER_INDEX = 2;
    private final H2Properties properties;

    @Autowired
    public H2WordRepository(H2Properties properties) {
        this.properties = properties;
    }

    @Override
    public void save(UUID requestId, List<String> words) {
        try (Connection connection = DriverManager.getConnection(properties.getUrl(), properties.getUsername(), properties.getPassword());
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            for (String word: words) {
                statement.setString(REQUEST_ID_PARAMETER_INDEX,requestId.toString());
                statement.setString(WORD_PARAMETER_INDEX, word);
                statement.addBatch();
            }
            statement.executeBatch();
        } catch (SQLException e) {
            throw new WordRepositoryException(e);
        }
    }
}
