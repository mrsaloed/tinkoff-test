package com.example.tinkofftest.exceptions.db;

import java.sql.SQLException;

public class SaveToDatabaseException extends RuntimeException{
    public SaveToDatabaseException(SQLException e) {
        super(e);
    }
}
