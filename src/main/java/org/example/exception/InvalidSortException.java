package org.example.exception;

public class InvalidSortException extends RuntimeException {

    public InvalidSortException(String sortBy) {
        super("Неизвестный тип сортировки: '" + sortBy + "'. Доступно: title, author, year");
    }
}