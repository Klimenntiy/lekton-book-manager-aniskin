package org.example.valueobject;

import org.example.exception.InvalidInputException;

public record Title(String value) {

    public Title {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException("Название книги не может быть пустым");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}