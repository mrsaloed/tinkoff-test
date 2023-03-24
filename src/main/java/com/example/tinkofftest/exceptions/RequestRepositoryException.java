package com.example.tinkofftest.exceptions;

import java.sql.SQLException;

public class RequestRepositoryException extends RuntimeException {
    public RequestRepositoryException(SQLException e) {
        super(e);
    }
}
