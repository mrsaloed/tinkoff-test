package com.example.tinkofftest.exceptions.db;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.sql.SQLException;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SaveToDatabaseException extends RuntimeException {

    public SaveToDatabaseException(SQLException e) {
        super(e);
    }

}
