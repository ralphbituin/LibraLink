import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class LibraLink {
    private static final Scanner input = new Scanner(System.in);
    private static final LibrarySystem library = new LibrarySystem();

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
        library.addBook(new Book("B001", "Noli Me Tangere", "Jose Rizal", "Fiction"));
        library.addBook(new Book("B002", "El Filibusterismo", "Jose Rizal", "Fiction"));
        library.addBook(new Book("B003", "Introduction to Java", "Herbert Schildt", "Programming"));
        library.addBook(new Book("B004", "Clean Code", "Robert C. Martin", "Programming"));

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
                    library.addBook(new Book(id, title, author, category));
                    System.out.println("Book added successfully.");
                    break;
                case 2:
                    Book book = library.findBook(readText("Enter Book ID to update: "));
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
        List<Book> results = library.searchBooks(choice, keyword);

        if (results.isEmpty()) {
            System.out.println("No matching books found.");
        } else {
            System.out.println("\nSearch results:");
            for (Book book : results) book.displayBook();
        }
    }

    private static void borrowBook() {
        System.out.println("\n--- BORROW A BOOK ---");
        String userId = readText("Borrower ID: ");
        User user = library.findUser(userId);
        if (user == null || user instanceof Librarian) {
            System.out.println("Borrower not found. Register a student, teacher, or staff member first.");
            return;
        }

        String bookId = readText("Book ID: ");
        Book book = library.findBook(bookId);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }

        try {
            Transaction transaction = library.borrowBook(book, user);
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
            Transaction transaction = library.returnBook(transactionId);
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
                    User user = null;
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
                    User found = library.findUser(readText("Enter User ID: "));
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

// ------------------------- BOOK CLASS -------------------------

class Book {
    private final String bookId;
    private String title;
    private String author;
    private String category;
    private boolean available;

    public Book(String bookId, String title, String author, String category) {
        this.bookId = bookId;
        this.title = title;
        this.author = author;
        this.category = category;
        this.available = true;
    }

    public String getBookId() { return bookId; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getCategory() { return category; }
    public boolean isAvailable() { return available; }

    public void setTitle(String title) { this.title = title; }
    public void setAuthor(String author) { this.author = author; }
    public void setCategory(String category) { this.category = category; }
    public void setAvailable(boolean available) { this.available = available; }

    public void displayBook() {
        System.out.println("----------------------------------------");
        System.out.println("Book ID: " + bookId);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Category: " + category);
        System.out.println("Status: " + (available ? "Available" : "Borrowed"));
    }
}

// ------------------------- USER CLASSES -------------------------

abstract class User {
    private final String userId;
    private String name;
    private String contactInfo;

    public User(String userId, String name, String contactInfo) {
        this.userId = userId;
        this.name = name;
        this.contactInfo = contactInfo;
    }

    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getContactInfo() { return contactInfo; }

    public void setName(String name) { this.name = name; }
    public void setContactInfo(String contactInfo) { this.contactInfo = contactInfo; }

    public abstract String getRole();

    public void displayUserInfo() {
        System.out.println("----------------------------------------");
        System.out.println("User ID: " + userId);
        System.out.println("Name: " + name);
        System.out.println("Role: " + getRole());
        System.out.println("Contact: " + contactInfo);
    }
}

class Student extends User {
    public Student(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Student"; }
}

class Teacher extends User {
    public Teacher(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Teacher"; }
}

class Staff extends User {
    public Staff(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Staff"; }
}

class Librarian extends User {
    public Librarian(String id, String name, String contact) {
        super(id, name, contact);
    }
    @Override public String getRole() { return "Librarian"; }
}

// ------------------------- TRANSACTION CLASS -------------------------

class Transaction {
    private final String transactionId;
    private final String bookId;
    private final String userId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public Transaction(String transactionId, String bookId, String userId,
                       LocalDate borrowDate, LocalDate dueDate) {
        this.transactionId = transactionId;
        this.bookId = bookId;
        this.userId = userId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
    }

    public String getTransactionId() { return transactionId; }
    public String getBookId() { return bookId; }
    public String getUserId() { return userId; }
    public LocalDate getBorrowDate() { return borrowDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public boolean isReturned() { return returnDate != null; }

    public void markReturned() {
        if (isReturned()) throw new IllegalStateException("This transaction is already closed.");
        returnDate = LocalDate.now();
    }

    public boolean isOverdue() {
        LocalDate comparisonDate = (returnDate == null) ? LocalDate.now() : returnDate;
        return comparisonDate.isAfter(dueDate);
    }

    public long getDaysOverdue() {
        LocalDate comparisonDate = (returnDate == null) ? LocalDate.now() : returnDate;
        return isOverdue() ? ChronoUnit.DAYS.between(dueDate, comparisonDate) : 0;
    }

    public void displayTransaction() {
        System.out.println("----------------------------------------");
        System.out.println("Transaction ID: " + transactionId);
        System.out.println("Book ID: " + bookId);
        System.out.println("Borrower ID: " + userId);
        System.out.println("Borrow date: " + borrowDate);
        System.out.println("Due date: " + dueDate);
        System.out.println("Return date: " + (returnDate == null ? "Not yet returned" : returnDate));
        System.out.println("Status: " + (isReturned() ? "Returned" : (isOverdue() ? "Overdue" : "Borrowed")));
    }
}

// ------------------------- LIBRARY SYSTEM CLASS -------------------------

class LibrarySystem {
    private final List<Book> books = new ArrayList<>();
    private final List<User> users = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private int nextTransactionNumber = 1;
    private static final int LOAN_PERIOD_DAYS = 7;

    public void addBook(Book book) {
        if (findBook(book.getBookId()) != null)
            throw new IllegalArgumentException("Duplicate book ID.");
        books.add(book);
    }

    public Book findBook(String id) {
        for (Book book : books)
            if (book.getBookId().equalsIgnoreCase(id)) return book;
        return null;
    }

    public void deleteBook(String id) {
        Book book = findBook(id);
        if (book == null) {
            System.out.println("Book not found.");
            return;
        }
        if (!book.isAvailable()) {
            System.out.println("Cannot delete a book that is currently borrowed.");
            return;
        }
        books.remove(book);
        System.out.println("Book deleted successfully.");
    }

    public void displayBooks() {
        if (books.isEmpty()) {
            System.out.println("No books in the library.");
            return;
        }
        for (Book book : books) book.displayBook();
    }

    public List<Book> searchBooks(int searchType, String keyword) {
        List<Book> results = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (Book book : books) {
            boolean match = false;
            switch (searchType) {
                case 1: match = book.getBookId().equalsIgnoreCase(keyword); break;
                case 2: match = book.getTitle().toLowerCase().contains(key); break;
                case 3: match = book.getAuthor().toLowerCase().contains(key); break;
                case 4: match = book.getCategory().toLowerCase().contains(key); break;
                default: return results;
            }
            if (match) results.add(book);
        }
        return results;
    }

    public void addUser(User user) {
        if (findUser(user.getUserId()) != null)
            throw new IllegalArgumentException("Duplicate user ID.");
        users.add(user);
    }

    public User findUser(String id) {
        for (User user : users)
            if (user.getUserId().equalsIgnoreCase(id)) return user;
        return null;
    }

    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        for (User user : users) user.displayUserInfo();
    }

    public Transaction borrowBook(Book book, User user) {
        if (!book.isAvailable())
            throw new IllegalStateException("Book Not Available.");
        book.setAvailable(false);

        LocalDate today = LocalDate.now();
        String transactionId = String.format("T%03d", nextTransactionNumber++);
        Transaction transaction = new Transaction(
                transactionId, book.getBookId(), user.getUserId(),
                today, today.plusDays(LOAN_PERIOD_DAYS));
        transactions.add(transaction);
        return transaction;
    }

    public Transaction returnBook(String transactionId) {
        Transaction transaction = findTransaction(transactionId);
        if (transaction == null)
            throw new IllegalArgumentException("Transaction not found.");
        if (transaction.isReturned())
            throw new IllegalStateException("This book has already been returned.");

        Book book = findBook(transaction.getBookId());
        if (book == null)
            throw new IllegalStateException("The book record for this transaction no longer exists.");

        transaction.markReturned();
        book.setAvailable(true);
        return transaction;
    }

    private Transaction findTransaction(String id) {
        for (Transaction transaction : transactions)
            if (transaction.getTransactionId().equalsIgnoreCase(id)) return transaction;
        return null;
    }

    public void displayTransactions() {
        System.out.println("\n--- TRANSACTION HISTORY ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
            return;
        }
        for (Transaction transaction : transactions) transaction.displayTransaction();
    }

    public void displayOverdueTransactions() {
        System.out.println("\n--- OVERDUE BOOKS ---");
        boolean found = false;
        for (Transaction transaction : transactions) {
            if (!transaction.isReturned() && transaction.isOverdue()) {
                transaction.displayTransaction();
                found = true;
            }
        }
        if (!found) System.out.println("There are no overdue books.");
    }
}
