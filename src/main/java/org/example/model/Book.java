package org.example.model;

import org.example.valueobjects.Author;
import org.example.valueobjects.Title;
import org.example.valueobjects.Year;

public record Book(int id, Title title, Author author, Year year) {

    @Override
    public String toString() {
        return String.format("[%d] %s | %s | %s", id, title, author, year);
    }
}