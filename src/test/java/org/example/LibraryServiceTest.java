package org.example;

import org.example.exception.BookNotFoundException;
import org.example.exception.DuplicateBookException;
import org.example.model.Book;
import org.example.service.LibraryService;
import org.example.valueobjects.Author;
import org.example.valueobjects.Title;
import org.example.valueobjects.Year;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LibraryServiceTest {

    private LibraryService service;

    @BeforeEach
    void setUp() {
        service = new LibraryService();
    }

    @Test
    void addBook_ShouldAddBookSuccessfully() {
        Title title = new Title("Война и мир");
        Author author = new Author("Толстой");
        Year year = new Year(1869);

        Book book = service.addBook(title, author, year);

        assertNotNull(book);
        assertEquals(1, book.id());
        assertEquals("Война и мир", book.title().value());
        assertEquals("Толстой", book.author().value());
        assertEquals(1869, book.year().value());
        assertEquals(1, service.getTotalCount());
    }

    @Test
    void addBook_ShouldThrowException_WhenDuplicateExists() {
        service.addBook(new Title("Мастер и Маргарита"), new Author("Булгаков"), new Year(1967));

        assertThrows(DuplicateBookException.class, () ->
                service.addBook(new Title("Мастер и Маргарита"), new Author("Булгаков"), new Year(2020)));
    }

    @Test
    void removeBook_ShouldRemoveAndReturnBook() {
        Book added = service.addBook(new Title("Идиот"), new Author("Достоевский"), new Year(1869));

        Book removed = service.removeBook(added.id());

        assertEquals(added, removed);
        assertEquals(0, service.getTotalCount());
    }

    @Test
    void removeBook_ShouldThrowException_WhenIdNotFound() {
        assertThrows(BookNotFoundException.class, () -> service.removeBook(999));
    }

    @Test
    void findBooks_ShouldFindByPartialTitle() {
        service.addBook(new Title("Преступление и наказание"), new Author("Достоевский"), new Year(1866));
        service.addBook(new Title("Идиот"), new Author("Достоевский"), new Year(1869));
        service.addBook(new Title("Отцы и дети"), new Author("Тургенев"), new Year(1862));

        List<Book> results = service.findBooks("преступление");

        assertEquals(1, results.size());
        assertEquals("Преступление и наказание", results.getFirst().title().value());
    }

    @Test
    void findBooks_ShouldFindByPartialAuthor() {
        service.addBook(new Title("Преступление и наказание"), new Author("Достоевский"), new Year(1866));
        service.addBook(new Title("Идиот"), new Author("Достоевский"), new Year(1869));
        service.addBook(new Title("Отцы и дети"), new Author("Тургенев"), new Year(1862));

        List<Book> results = service.findBooks("досто");

        assertEquals(2, results.size());
    }

    @Test
    void getAllBooks_ShouldSortByYear() {
        service.addBook(new Title("Книга 1"), new Author("Автор"), new Year(2000));
        service.addBook(new Title("Книга 2"), new Author("Автор"), new Year(1990));
        service.addBook(new Title("Книга 3"), new Author("Автор"), new Year(2010));

        List<Book> sorted = service.getAllBooks("year");

        assertEquals(1990, sorted.get(0).year().value());
        assertEquals(2000, sorted.get(1).year().value());
        assertEquals(2010, sorted.get(2).year().value());
    }

    @Test
    void getStatistics_ShouldReturnCorrectInfo() {
        service.addBook(new Title("Война и мир"), new Author("Толстой"), new Year(1869));
        service.addBook(new Title("Анна Каренина"), new Author("Толстой"), new Year(1877));
        service.addBook(new Title("Преступление и наказание"), new Author("Достоевский"), new Year(1866));

        String stats = service.getStatistics();

        assertTrue(stats.contains("Всего книг: 3"));
        assertTrue(stats.contains("Самая старая книга"));
        assertTrue(stats.contains("1866"));
        assertTrue(stats.contains("Самая новая книга"));
        assertTrue(stats.contains("1877"));
        assertTrue(stats.contains("Толстой: 2"));
        assertTrue(stats.contains("Достоевский: 1"));
    }
}