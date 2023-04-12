package com.example.tinkofftest.services.impl;

import com.example.tinkofftest.entities.RequestEntity;
import com.example.tinkofftest.exceptions.db.SaveToDatabaseException;
import com.example.tinkofftest.properties.H2Properties;
import com.example.tinkofftest.services.LogToDbService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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


    @Override
    public void logSuccess(RequestEntity request, String ip) {
        try (Connection connection = getNewConnection()) {
            saveRequestWithWords(connection, request, ip);
        } catch (SQLException e) {
            throw new SaveToDatabaseException(e);
        }
    }

    @Override
    public void logFailure(RequestEntity request, String ip) {
        try (Connection connection = getNewConnection()) {
            saveRequest(connection, request, ip);
        } catch (SQLException e) {
            throw new SaveToDatabaseException(e);
        }
    }

    /**
     * Transactional method to add information into Requests and Words tables.
     * @param connection Connection to DB.
     * @param request RequestEntity that would be added into DB.
     * @param ip IP address from which request was sent.
     * @throws SQLException thrown if some problems with adding to DB.
     */
    private void saveRequestWithWords(Connection connection,
                                      RequestEntity request,
                                      String ip) throws SQLException {
        connection.setAutoCommit(false);
        try {
            saveRequest(connection, request, ip);
            saveWords(connection, request);
        } catch (SQLException e) {
            connection.rollback();
            connection.setAutoCommit(true);
            throw e;
        }
        connection.commit();
        connection.setAutoCommit(true);
    }

    /**
     * Getting Connection to DB.
     * @return new Connection to DB from DriverManager.
     * @throws SQLException thrown if some problems with creation connection.
     */
    private Connection getNewConnection() throws SQLException {
        String url = properties.getUrl();
        String username = properties.getUsername();
        String password = properties.getPassword();
        return DriverManager.getConnection(url, username, password);
    }

    /**
     * Adding information into Requests table.
     * @param connection Connection to DB.
     * @param request RequestEntity that would be added into DB.
     * @param ip IP address from which request was sent.
     * @throws SQLException thrown if some problems with saving RequestEntity.
     */
    private void saveRequest(Connection connection,
                             RequestEntity request,
                             String ip) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_REQUEST_TABLE)) {
            statement.setString(REQUEST_ID_PARAMETER_INDEX, request.getId().toString());
            statement.setString(REQUEST_MESSAGE_PARAMETER_INDEX, request.getInputData());
            statement.setString(RESPONSE_MESSAGE_PARAMETER_INDEX, request.getOutputData());
            statement.setTime(TIME_PARAMETER_INDEX, request.getRequestTime());
            statement.setString(TRANSLATE_PARAMS_PARAMETER_INDEX, request.getParameters());
            statement.setString(IP_PARAMETER_INDEX, ip);
            statement.executeUpdate();
        }
    }

    /**
     * Adding information into Words table.
     * @param connection Connection to db.
     * @param request RequestEntity that would be added into DB.
     * @throws SQLException thrown if some problems with saving words.
     */
    private void saveWords(Connection connection, RequestEntity request) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(INSERT_WORD_TABLE)) {
            for (String word : request.getTranslatedWords()) {
                statement.setString(REQUEST_ID_PARAMETER_INDEX, request.getId().toString());
                statement.setString(WORD_PARAMETER_INDEX, word);
                statement.addBatch();
            }
            statement.executeBatch();
        }
    }
}
