package src;

import src.classes.*;
import src.classes.user.*;
import java.util.List;
import java.util.Scanner;


public class LibraLink {
    private static final Scanner input = new Scanner(System.in);
    private static final library library = new library();

    public static void main(String[] args) {
        seedSampleData();

        boolean running = true;
        while (running) {
            System.out.println("\n========================================");
            System.out.println("       LIBRALINK LIBRARY SYSTEM");
            System.out.println("========================================");
            System.out.println("1. Book Management");
            System.out.println("2. Search Books");
            System.out.println("3. Borrow a Book");
            System.out.println("4. Return a Book");
            System.out.println("5. Borrower Management");
            System.out.println("6. View Transactions");
            System.out.println("7. View Overdue Books");
            System.out.println("0. Exit");

            int choice = readInt("Enter choice: ");
            switch (choice) {
                case 1: bookMenu(); break;
                case 2: searchBooks(); break;
                case 3: borrowBook(); break;
                case 4: returnBook(); break;
                case 5: borrowerMenu(); break;
                case 6: library.displayTransactions(); break;
                case 7: library.displayOverdueTransactions(); break;
                case 0:
                    running = false;
                    System.out.println("Thank you for using LibraLink!");
                    break;
                default: System.out.println("Invalid menu choice.");
            }
        }
        input.close();
    }

    private static void seedSampleData() {
        library.addBook(new book("B001", "Noli Me Tangere", "Jose Rizal", "Fiction"));
        library.addBook(new book("B002", "El Filibusterismo", "Jose Rizal", "Fiction"));
        library.addBook(new book("B003", "Introduction to Java", "Herbert Schildt", "Programming"));
        library.addBook(new book("B004", "Clean Code", "Robert C. Martin", "Programming"));
        library.addUser(new Student("U001", "Juan Dela Cruz", "09123456789"));
        library.addUser(new Teacher("U002", "Maria Santos", "09987654321"));
        library.addUser(new Staff("U003", "Pedro Reyes", "09112223334"));
        library.addUser(new Librarian("U004", "Ana Garcia", "09223334455"));
    }

    private static void bookMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- BOOK MANAGEMENT ---");
            System.out.println("1. Add Book");
            System.out.println("2. Update Book");
            System.out.println("3. Delete Book");
            System.out.println("4. View All Books");
            System.out.println("0. Back");
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    String id = readText("Book ID: ");
                    if (library.findBook(id) != null) {
                        System.out.println("That book ID already exists.");
                        break;
                    }
                    String title = readText("Title: ");
                    String author = readText("Author: ");
                    String category = readText("Category: ");
                    library.addBook(new book(id, title, author, category));
                    System.out.println("Book added successfully.");
                    break;
                case 2:
                    book book = library.findBook(readText("Enter Book ID to update: "));
                    if (book == null) {
                        System.out.println("Book not found.");
                        break;
                    }
                    if (!book.isAvailable()) {
                        System.out.println("A borrowed book cannot be edited in this version.");
                        break;
                    }
                    book.setTitle(readText("New title: "));
                    book.setAuthor(readText("New author: "));
                    book.setCategory(readText("New category: "));
                    System.out.println("Book updated successfully.");
                    break;
                case 3:
                    library.deleteBook(readText("Enter Book ID to delete: "));
                    break;
                case 4:
                    library.displayBooks();
                    break;
                case 0: back = true; break;
                default: System.out.println("Invalid menu choice.");
            }
        }
    }

    private static void searchBooks() {
        System.out.println("\n--- SEARCH BOOKS ---");
        System.out.println("1. Search by Book ID");
        System.out.println("2. Search by Title");
        System.out.println("3. Search by Author");
        System.out.println("4. Search by Category");
        int choice = readInt("Choose search type: ");
        String keyword = readText("Enter search keyword: ");
        List<book> results = library.searchBooks(choice, keyword);

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            System.out.println("\nSearch results:");
            for (book book : results) book.displayBook();
        }
    }

    private static void borrowBook() {
        System.out.println("\n--- BORROW A BOOK ---");
        String userId = readText("Borrower ID: ");
        user user = library.findUser(userId);
        if (user == null || user instanceof Librarian) {
            System.out.println("Borrower not found. Register a student, teacher, or staff member first.");
            return;
        }

        String bookId = readText("Book ID: ");
        book book = library.findBook(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        try {
            transaction transaction = library.borrowBook(book, user);
            System.out.println("Book borrowed successfully!");
            System.out.println("Transaction ID: " + transaction.getTransactionId());
            System.out.println("Book: " + book.getTitle());
            System.out.println("Borrower: " + user.getName());
            System.out.println("Due date: " + transaction.getDueDate());
        } catch (IllegalStateException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void returnBook() {
        System.out.println("\n--- RETURN A BOOK ---");
        String transactionId = readText("Transaction ID: ");
        try {
            transaction transaction = library.returnBook(transactionId);
            System.out.println("Book returned successfully.");
            System.out.println("Book ID: " + transaction.getBookId());
            System.out.println("Return date: " + transaction.getReturnDate());
            if (transaction.isOverdue()) {
                System.out.println("This book was returned " + transaction.getDaysOverdue()
                        + " day(s) late.");
            } else {
                System.out.println("Returned on or before the due date.");
            }
        } catch (IllegalArgumentException | IllegalStateException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void borrowerMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- BORROWER MANAGEMENT ---");
            System.out.println("1. Add Borrower");
            System.out.println("2. View All Users");
            System.out.println("3. Search User by ID");
            System.out.println("0. Back");
            int choice = readInt("Enter choice: ");

            switch (choice) {
                case 1:
                    String id = readText("User ID: ");
                    if (library.findUser(id) != null) {
                        System.out.println("That user ID already exists.");
                        break;
                    }
                    String name = readText("Full name: ");
                    String contact = readText("Contact information: ");
                    System.out.println("Role: 1. Student  2. Teacher  3. Staff  4. Librarian");
                    int role = readInt("Choose role: ");
                    user user = null;
                    switch (role) {
                        case 1: user = new Student(id, name, contact); break;
                        case 2: user = new Teacher(id, name, contact); break;
                        case 3: user = new Staff(id, name, contact); break;
                        case 4: user = new Librarian(id, name, contact); break;
                        default:
                            System.out.println("Invalid role. User was not added.");
                            break;
                    }
                    if (role >= 1 && role <= 4) {
                        library.addUser(user);
                        System.out.println("User added successfully.");
                    }
                    break;
                case 2:
                    library.displayUsers();
                    break;
                case 3:
                    user found = library.findUser(readText("Enter User ID: "));
                    if (found == null) System.out.println("User not found.");
                    else found.displayUserInfo();
                    break;
                case 0: back = true; break;
                default: System.out.println("Invalid menu choice.");
            }
        }
    }

    private static String readText(String prompt) {
        System.out.print(prompt);
        return input.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            String value = input.nextLine().trim();
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException ex) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
