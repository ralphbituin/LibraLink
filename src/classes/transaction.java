package src.classes;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class transaction {
    private final String transactionId;
    private final String bookId;
    private final String userId;
    private final LocalDate borrowDate;
    private final LocalDate dueDate;
    private LocalDate returnDate;

    public transaction(String transactionId, String bookId, String userId,
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