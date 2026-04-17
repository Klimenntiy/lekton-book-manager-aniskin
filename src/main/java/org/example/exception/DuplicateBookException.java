package org.example.exception;


public class DuplicateBookException extends RuntimeException {

    public DuplicateBookException(String title, String author) {
        super("Книга с названием '" + title + "' и автором '" + author + "' уже существует");
    }
}