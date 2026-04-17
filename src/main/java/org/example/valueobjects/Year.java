package org.example.valueobjects;

import org.example.exception.InvalidInputException;

import java.time.LocalDate;

public record Year(int value) {

    public Year(String input) {
        this(parseYear(input));
    }

    private static int parseYear(String input) {
        if (input == null || input.isBlank()) {
            throw new InvalidInputException("Год не может быть пустым");
        }

        int yearValue;
        try {
            yearValue = Integer.parseInt(input.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("Год должен быть целым числом");
        }

        if (yearValue <= 0) {
            throw new InvalidInputException("Год должен быть положительным числом");
        }

        int currentYear = LocalDate.now().getYear();
        if (yearValue > currentYear) {
            throw new InvalidInputException("Год не может быть больше текущего (" + currentYear + ")");
        }

        return yearValue;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}