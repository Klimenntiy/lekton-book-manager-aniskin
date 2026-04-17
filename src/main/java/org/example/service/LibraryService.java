package org.example.service;

import org.example.exception.*;
import org.example.model.Book;
import org.example.valueobjects.Author;
import org.example.valueobjects.Title;
import org.example.valueobjects.Year;

import java.util.*;

public class LibraryService {

    private final Map<Integer, Book> books = new HashMap<>();
    private int nextId = 1;

    public Book addBook(Title title, Author author, Year year) {
        String titleStr = title.value();
        String authorStr = author.value();

        for (Book book : books.values()) {
            boolean sameTitle = book.title().value().equalsIgnoreCase(titleStr);
            boolean sameAuthor = book.author().value().equalsIgnoreCase(authorStr);

            if (sameTitle && sameAuthor) {
                throw new DuplicateBookException(titleStr, authorStr);
            }
        }

        Book newBook = new Book(nextId, title, author, year);
        books.put(nextId, newBook);
        nextId++;
        return newBook;
    }

    public Book removeBook(int id) {
        Book removed = books.remove(id);

        if (removed == null) {
            throw new BookNotFoundException(id);
        }

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

        StringBuilder stats = new StringBuilder();
        stats.append("Всего книг: ").append(books.size()).append("\n");

        Book oldest = null;
        for (Book book : books.values()) {
            if (oldest == null || book.year().value() < oldest.year().value()) {
                oldest = book;
            }
        }
        stats.append("Самая старая книга: ").append(oldest).append("\n");

        Book newest = null;
        for (Book book : books.values()) {
            if (newest == null || book.year().value() > newest.year().value()) {
                newest = book;
            }
        }
        stats.append("Самая новая книга: ").append(newest).append("\n");

        Map<String, Integer> authorCount = new HashMap<>();
        for (Book book : books.values()) {
            String author = book.author().value();
            int currentCount = authorCount.getOrDefault(author, 0);
            authorCount.put(author, currentCount + 1);
        }

        stats.append("Топ авторов по количеству книг:\n");
        authorCount.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .limit(3)
                .forEach(entry -> stats.append(" - ")
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