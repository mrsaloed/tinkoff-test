package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.exceptions.db.SaveToDatabaseException;
import com.example.tinkofftest.properties.H2Properties;
import com.example.tinkofftest.services.LogToDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequestScope
public class LogToDbServiceImpl implements LogToDbService {

    private static final String INSERT_REQUEST_TABLE = "INSERT INTO Requests values(?,?,?,?,?,?)";
    private static final String INSERT_WORD_TABLE = "INSERT INTO Words VALUES (?,?)";
    private static final int REQUEST_ID_PARAMETER_INDEX = 1;
    private static final int REQUEST_MESSAGE_PARAMETER_INDEX = 2;
    private static final int WORD_PARAMETER_INDEX = 2;
    private static final int RESPONSE_MESSAGE_PARAMETER_INDEX = 3;
    private static final int TIME_PARAMETER_INDEX = 4;
    private static final int TRANSLATE_PARAMS_PARAMETER_INDEX = 5;
    private static final int IP_PARAMETER_INDEX = 6;
    private final H2Properties properties;


    @Autowired
    public LogToDbServiceImpl(H2Properties properties) {
        this.properties = properties;
    }

    public void log(String inputData,
                    String outputData,
                    String parameters,
                    List<String> translatedWords,
                    String ip) {
        UUID requestId = UUID.randomUUID();
        try (Connection connection = getNewConnection()) {
            connection.setAutoCommit(false);
            try {
                saveRequest(connection, requestId.toString(), inputData, outputData, parameters, ip);
                saveWords(connection, requestId.toString(), translatedWords);
            } catch (SQLException e) {
                connection.rollback();
                connection.setAutoCommit(true);
                throw e;
            }
            connection.commit();
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            throw new SaveToDatabaseException(e);
        }
    }

    private Connection getNewConnection() throws SQLException {
        String url = properties.getUrl();
        String username = properties.getUsername();
        String password = properties.getPassword();
        return DriverManager.getConnection(url, username, password);
    }

    private void saveRequest(Connection connection,
                             String requestId,
                             String inputData,
                             String outputData,
                             String parameters,
                             String ip) throws SQLException {
        Time time = Time.valueOf(LocalDateTime.now().toLocalTime());
        try (PreparedStatement insertRequestTable = connection.prepareStatement(INSERT_REQUEST_TABLE)) {
            insertRequestTable.setString(REQUEST_ID_PARAMETER_INDEX, requestId);
            insertRequestTable.setString(REQUEST_MESSAGE_PARAMETER_INDEX, inputData);
            insertRequestTable.setString(RESPONSE_MESSAGE_PARAMETER_INDEX, outputData);
            insertRequestTable.setTime(TIME_PARAMETER_INDEX, time);
            insertRequestTable.setString(TRANSLATE_PARAMS_PARAMETER_INDEX, parameters);
            insertRequestTable.setString(IP_PARAMETER_INDEX, ip);
            insertRequestTable.executeUpdate();
        }
    }

    private void saveWords(Connection connection, String requestId, List<String> translatedWords) throws SQLException {
        try (PreparedStatement insertWordTable = connection.prepareStatement(INSERT_WORD_TABLE)) {
            if (translatedWords != null) {
                for (String word : translatedWords) {
                    insertWordTable.setString(REQUEST_ID_PARAMETER_INDEX, requestId);
                    insertWordTable.setString(WORD_PARAMETER_INDEX, word);
                    insertWordTable.addBatch();
                }
                insertWordTable.executeBatch();
            }
        }
    }
}
