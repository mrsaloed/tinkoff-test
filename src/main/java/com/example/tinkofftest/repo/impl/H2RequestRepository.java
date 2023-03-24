package com.example.tinkofftest.repo.impl;

import com.example.tinkofftest.exceptions.RequestRepositoryException;
import com.example.tinkofftest.properties.H2Properties;
import com.example.tinkofftest.repo.RequestsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.time.LocalDateTime;

import java.util.UUID;

@Repository
public class H2RequestRepository implements RequestsRepository {

    private static final String SQL_INSERT = "INSERT INTO Requests values(?,?,?,?,?,?)";
    private static final int REQUEST_ID_PARAMETER_INDEX = 1;
    private static final int REQUEST_MESSAGE_PARAMETER_INDEX = 2;
    private static final int RESPONSE_MESSAGE_PARAMETER_INDEX = 3;
    private static final int TIME_PARAMETER_INDEX = 4;
    private static final int TRANSLATE_PARAMS_PARAMETER_INDEX = 5;
    private static final int IP_PARAMETER_INDEX = 6;
    private final H2Properties properties;


    @Autowired
    public H2RequestRepository(H2Properties properties) {
        this.properties = properties;
    }

    @Override
    public void save(UUID requestId, String requestMessage, String responseMessage, String params, String ip) {
        Time time = Time.valueOf(LocalDateTime.now().toLocalTime());
        try (Connection connection = DriverManager.getConnection(properties.getUrl(), properties.getUsername(), properties.getPassword());
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            statement.setString(REQUEST_ID_PARAMETER_INDEX, requestId.toString());
            statement.setString(REQUEST_MESSAGE_PARAMETER_INDEX, requestMessage);
            statement.setString(RESPONSE_MESSAGE_PARAMETER_INDEX, responseMessage);
            statement.setTime(TIME_PARAMETER_INDEX, time);
            statement.setString(TRANSLATE_PARAMS_PARAMETER_INDEX, params);
            statement.setString(IP_PARAMETER_INDEX, ip);
            statement.executeUpdate();
        } catch (SQLException e) {
            throw new RequestRepositoryException(e);
        }
    }
}
