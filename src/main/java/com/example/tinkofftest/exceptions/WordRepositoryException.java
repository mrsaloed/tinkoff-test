package com.example.tinkofftest.exceptions;

import java.sql.SQLException;

public class WordRepositoryException extends RuntimeException{
    public WordRepositoryException(SQLException e) {
        super(e);
    }
}
