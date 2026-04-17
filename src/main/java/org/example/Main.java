package org.example;

import org.example.exception.*;
import org.example.model.Book;
import org.example.service.LibraryService;
import org.example.valueobjects.Author;
import org.example.valueobjects.Title;
import org.example.valueobjects.Year;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final LibraryService service = new LibraryService();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("    Библиотечный менеджер    ");
        System.out.println("Введите HELP для списка команд");
        System.out.println("Введите EXIT для выхода");

        while (true) {
            System.out.print("\n> ");
            String line = scanner.nextLine().trim();

            if (line.isEmpty()) {
                continue;
            }

            String[] parts = line.split("\\s+", 2);
            String command = parts[0].toUpperCase();
            String arguments = parts.length > 1 ? parts[1] : "";

            try {
                boolean shouldExit = processCommand(command, arguments);
                if (shouldExit) {
                    break;
                }
            } catch (RuntimeException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }

        scanner.close();
    }

    private static boolean processCommand(String command, String arguments) {
        switch (command) {
            case "ADD":
                handleAdd(arguments);
                break;
            case "REMOVE":
                handleRemove(arguments);
                break;
            case "LIST":
                handleList(arguments);
                break;
            case "FIND":
                handleFind(arguments);
                break;
            case "STATS":
                handleStats();
                break;
            case "HELP":
                showHelp();
                break;
            case "EXIT":
                System.out.println("До свидания!");
                return true;
            default:
                System.out.println("Неизвестная команда: " + command);
                System.out.println("Введите HELP для списка команд");
        }
        return false;
    }

    private static void showHelp() {
        System.out.println();
        System.out.println("Доступные команды:");
        System.out.println("  ADD <название>;<автор>;<год>  - добавить книгу");
        System.out.println("  REMOVE <id>                    - удалить книгу по ID");
        System.out.println("  LIST                           - показать все книги (по ID)");
        System.out.println("  LIST title                     - сортировка по названию");
        System.out.println("  LIST author                    - сортировка по автору");
        System.out.println("  LIST year                      - сортировка по году");
        System.out.println("  FIND <запрос>                  - поиск по названию или автору");
        System.out.println("  STATS                          - статистика библиотеки");
        System.out.println("  HELP                           - показать эту справку");
        System.out.println("  EXIT                           - выход");
        System.out.println();
        System.out.println("Примеры:");
        System.out.println("  ADD Война и мир;Толстой;1869");
        System.out.println("  FIND толстой");
        System.out.println("  LIST year");
    }

    private static void handleAdd(String arguments) {
        if (arguments.isEmpty()) {
            throw new InvalidInputException("Укажите данные книги. Формат: ADD <название>;<автор>;<год>");
        }

        String[] parts = arguments.split(";");
        if (parts.length != 3) {
            throw new InvalidInputException("Неверный формат. Ожидается: <название>;<автор>;<год>");
        }

        String titleStr = parts[0].trim();
        String authorStr = parts[1].trim();
        String yearStr = parts[2].trim();

        Title title = new Title(titleStr);
        Author author = new Author(authorStr);
        Year year = new Year(yearStr);

        Book book = service.addBook(title, author, year);
        System.out.println("Книга добавлена: " + book);
    }

    private static void handleRemove(String arguments) {
        if (arguments.isEmpty()) {
            throw new InvalidInputException("Укажите ID книги. Формат: REMOVE <id>");
        }

        int id;
        try {
            id = Integer.parseInt(arguments.trim());
        } catch (NumberFormatException e) {
            throw new InvalidInputException("ID должен быть целым числом");
        }

        Book removed = service.removeBook(id);
        System.out.println("Книга удалена: " + removed);
    }

    private static void handleList(String arguments) {
        String sortBy = arguments.isEmpty() ? null : arguments.trim();
        List<Book> books = service.getAllBooks(sortBy);

        if (books.isEmpty()) {
            System.out.println("Библиотека пуста.");
            return;
        }

        String sortInfo = (sortBy == null) ? "порядок добавления" : sortBy;
        System.out.println("Список книг (сортировка: " + sortInfo + "):");
        for (Book book : books) {
            System.out.println("  " + book);
        }
        System.out.println("Всего: " + books.size() + " кн.");
    }

    private static void handleFind(String arguments) {
        if (arguments.isEmpty()) {
            throw new InvalidInputException("Укажите поисковый запрос. Формат: FIND <запрос>");
        }

        String query = arguments.trim();
        if (query.isEmpty()) {
            throw new InvalidInputException("Поисковый запрос не может быть пустым");
        }

        List<Book> results = service.findBooks(query);
        if (results.isEmpty()) {
            System.out.println("По запросу \"" + query + "\" ничего не найдено.");
            return;
        }

        System.out.println("Найдено книг: " + results.size());
        for (Book book : results) {
            System.out.println("  " + book);
        }
    }

    private static void handleStats() {
        System.out.println(service.getStatistics());
    }
}