package org.example.valueobjects;

import org.example.exception.InvalidInputException;

public record Author(String value) {

    public Author {
        if (value == null || value.isBlank()) {
            throw new InvalidInputException("Автор не может быть пустым");
        }
    }

    @Override
    public String toString() {
        return value;
    }
}