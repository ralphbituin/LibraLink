package src.classes;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class library {
    private final List<book> books = new ArrayList<>();
    private final List<user> users = new ArrayList<>();
    private final List<transaction> transactions = new ArrayList<>();
    private int nextTransactionNumber = 1;
    private static final int LOAN_PERIOD_DAYS = 7;

    public void addBook(book book) {
        if (findBook(book.getBookId()) != null)
            throw new IllegalArgumentException("Duplicate book ID.");
        books.add(book);
    }

    public book findBook(String id) {
        for (book book : books)
            if (book.getBookId().equalsIgnoreCase(id)) return book;
        return null;
    }

    public void deleteBook(String id) {
        book book = findBook(id);
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
        for (book book : books) book.displayBook();
    }

    public List<book> searchBooks(int searchType, String keyword) {
        List<book> results = new ArrayList<>();
        String key = keyword.toLowerCase();
        for (book book : books) {
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

    public void addUser(user user) {
        if (findUser(user.getUserId()) != null)
            throw new IllegalArgumentException("Duplicate user ID.");
        users.add(user);
    }

    public user findUser(String id) {
        for (user user : users)
            if (user.getUserId().equalsIgnoreCase(id)) return user;
        return null;
    }

    public void displayUsers() {
        if (users.isEmpty()) {
            System.out.println("No users registered.");
            return;
        }
        for (user user : users) user.displayUserInfo();
    }

    public transaction borrowBook(book book, user user) {
        if (!book.isAvailable())
            throw new IllegalStateException("Book Not Available.");
        book.setAvailable(false);

        LocalDate today = LocalDate.now();
        String transactionId = String.format("T%03d", nextTransactionNumber++);
        transaction transaction = new transaction(
                transactionId, book.getBookId(), user.getUserId(),
                today, today.plusDays(LOAN_PERIOD_DAYS));
        transactions.add(transaction);
        return transaction;
    }

    public transaction returnBook(String transactionId) {
        transaction transaction = findTransaction(transactionId);
        if (transaction == null)
            throw new IllegalArgumentException("Transaction not found.");
        if (transaction.isReturned())
            throw new IllegalStateException("This book has already been returned.");

        book book = findBook(transaction.getBookId());
        if (book == null)
            throw new IllegalStateException("The book record for this transaction no longer exists.");

        transaction.markReturned();
        book.setAvailable(true);
        return transaction;
    }

    private transaction findTransaction(String id) {
        for (transaction transaction : transactions)
            if (transaction.getTransactionId().equalsIgnoreCase(id)) return transaction;
        return null;
    }

    public void displayTransactions() {
        System.out.println("\n--- TRANSACTION HISTORY ---");
        if (transactions.isEmpty()) {
            System.out.println("No transactions recorded.");
            return;
        }
        for (transaction transaction : transactions) transaction.displayTransaction();
    }

    public void displayOverdueTransactions() {
        System.out.println("\n--- OVERDUE BOOKS ---");
        boolean found = false;
        for (transaction transaction : transactions) {
            if (!transaction.isReturned() && transaction.isOverdue()) {
                transaction.displayTransaction();
                found = true;
            }
        }
        if (!found) System.out.println("There are no overdue books.");
    }
}
