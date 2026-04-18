package org.example.service;

import org.example.exception.*;
import org.example.model.Book;
import org.example.valueobject.*;


import java.util.*;

public class LibraryService {

    private final Map<Integer, Book> books = new HashMap<>();
    private final Map<String, Integer> duplicateIndex = new HashMap<>();
    private int nextId = 1;

    public Book addBook(Title title, Author author, Year year) {
        String titleStr = title.value();
        String authorStr = author.value();
        String key = (titleStr + "|" + authorStr).toLowerCase();

        if (duplicateIndex.containsKey(key)) {
            throw new DuplicateBookException(titleStr, authorStr);
        }

        Book newBook = new Book(nextId, title, author, year);
        books.put(nextId, newBook);
        duplicateIndex.put(key, nextId);
        nextId++;
        return newBook;
    }

    public Book removeBook(int id) {
        Book removed = books.remove(id);
        if (removed == null) {
            throw new BookNotFoundException(id);
        }

        String key = (removed.title().value() + "|" + removed.author().value()).toLowerCase();
        duplicateIndex.remove(key);

        return removed;
    }

    public List<Book> getAllBooks(String sortBy) {
        List<Book> allBooks = new ArrayList<>(books.values());

        if (sortBy == null || sortBy.isEmpty()) {
            allBooks.sort(Comparator.comparingInt(Book::id));
            return allBooks;
        }

        switch (sortBy.toLowerCase()) {
            case "title":
                allBooks.sort((book1, book2) ->
                        book1.title().value().compareToIgnoreCase(book2.title().value()));
                break;
            case "author":
                allBooks.sort((book1, book2) ->
                        book1.author().value().compareToIgnoreCase(book2.author().value()));
                break;
            case "year":
                allBooks.sort(Comparator.comparingInt(b -> b.year().value()));
                break;
            default:
                throw new InvalidSortException(sortBy);
        }
        return allBooks;
    }

    public List<Book> findBooks(String query) {
        List<Book> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (Book book : books.values()) {
            boolean matchesTitle = book.title().value().toLowerCase().contains(lowerQuery);
            boolean matchesAuthor = book.author().value().toLowerCase().contains(lowerQuery);
            if (matchesTitle || matchesAuthor) {
                result.add(book);
            }
        }
        return result;
    }

    public String getStatistics() {
        if (books.isEmpty()) {
            return "Библиотека пуста.";
        }

        int total = books.size();
        Book oldest = null;
        Book newest = null;
        Map<String, Integer> authorCount = new HashMap<>();

        for (Book book : books.values()) {
            if (oldest == null || book.year().value() < oldest.year().value()) {
                oldest = book;
            }
            if (newest == null || book.year().value() > newest.year().value()) {
                newest = book;
            }

            authorCount.merge(book.author().value(), 1, Integer::sum);
        }

        StringBuilder stats = new StringBuilder();
        stats.append("Всего книг: ").append(total).append("\n");
        stats.append("Самая старая книга: ").append(oldest).append("\n");
        stats.append("Самая новая книга: ").append(newest).append("\n");

        stats.append("Топ авторов по количеству книг:\n");
        authorCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .forEach(entry -> stats.append("  - ")
                        .append(entry.getKey())
                        .append(": ")
                        .append(entry.getValue())
                        .append(" кн.\n"));

        return stats.toString();
    }

    public int getTotalCount() {
        return books.size();
    }
}