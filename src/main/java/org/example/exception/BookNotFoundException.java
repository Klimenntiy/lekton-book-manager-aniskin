package org.example.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(int id) {
        super("Книга с ID " + id + " не найдена");
    }
}