package org.example.valueobjects;

import org.example.exception.InvalidInputException;

import java.time.LocalDate;

public record Year(int value) {

    public Year {
        int currentYear = LocalDate.now().getYear();

        if (value <= 0) {
            throw new InvalidInputException("Год должен быть положительным числом");
        }
        if (value > currentYear) {
            throw new InvalidInputException("Год не может быть больше текущего (" + currentYear + ")");
        }
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}