package org.example.model;

import org.example.valueobject.Author;
import org.example.valueobject.Title;
import org.example.valueobject.Year;

public record Book(int id, Title title, Author author, Year year) {

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s", id, title, author, year);
    }
}