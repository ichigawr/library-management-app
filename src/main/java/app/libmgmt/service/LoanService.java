package app.libmgmt.service;

import app.libmgmt.dao.LoanDAO;
import app.libmgmt.model.Book;
import app.libmgmt.model.Loan;
import app.libmgmt.model.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoanService {
    private final LoanDAO loanDAO;
    private final BookService bookService;
    private final UserService userService;

    public LoanService() {
        this.loanDAO = new LoanDAO();
        this.bookService = new BookService();
        this.userService = new UserService();
    }

    public void addLoan(Loan loan) {
        try {
            loanDAO.addLoan(loan);
            System.out.println("Loan added successfully");
        } catch (SQLException e) {
            throw new ServiceException("Error adding loan", e);
        }
    }

    public void updateLoan(Loan loan) {
        try {
            loanDAO.updateLoan(loan);
            System.out.println("Loan updated successfully");
        } catch (SQLException e) {
            throw new ServiceException("Error updating loan", e);
        }
    }

    public void deleteLoanById(int loanId) {
        try {
            loanDAO.deleteLoanById(loanId);
            System.out.println("Loan deleted successfully");
        } catch (SQLException e) {
            throw new ServiceException("Error deleting loan", e);
        }
    }

    public void deleteLoanByUserId(String userId) {
        try {
            loanDAO.deleteLoanByUserId(userId);
            System.out.println("Loan deleted successfully");
        } catch (SQLException e) {
            throw new ServiceException("Error deleting loan", e);
        }
    }

    public List<Loan> getAllLoans() {
        try {
            return loanDAO.getAllLoans();
        } catch (SQLException e) {
            throw new ServiceException("Error getting all loans", e);
        }
    }

    public List<Loan> getOverdueLoans() {
        try {
            return loanDAO.getOverdueLoans();
        } catch (SQLException e) {
            throw new ServiceException("Error getting overdue loans", e);
        }
    }

    public void markOverdueLoans() {
        try {
            loanDAO.markOverdueLoans();
        } catch (SQLException e) {
            throw new ServiceException("Error mark Overdue Loans", e);
        }
    }

    public List<Loan> getLoansByUserId(String userId) {
        try {
            return loanDAO.getLoansByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting loan by userId", e);
        }
    }

    public List<Loan> getBorrowedLoansByUserId(String userId) {
        try {
            return loanDAO.getBorrowedLoansByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting loan by userId", e);
        }
    }

    public List<Loan> getReturnLoansByUserId(String userId) {
        try {
            return loanDAO.getReturnLoansByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting loan by userId", e);
        }
    }

    public int countTotalBorrowedBooks() {
        try {
            return loanDAO.countTotalBorrowedBooks();
        } catch (SQLException e) {
            throw new ServiceException("Error getting loan by userId", e);
        }
    }

    public int getMaxLoanId() {
        try {
            return loanDAO.getMaxLoanId();
        } catch (SQLException e) {
            throw new ServiceException("Error getting min loan id", e);
        }
    }

    public String getIsbnByUserId(String userId) {
        try {
            return loanDAO.getIsbnByUserId(userId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting isbn by user id", e);
        }
    }

    public List<Book> getBookFromLoan(String string_isbn) {
        List<Book> books = new ArrayList<>();
        String[] isbnArray = string_isbn.split(",\\s*");
        
        for (String x : isbnArray) {
            Book book = bookService.getBookByIsbn(x);
            if (book != null) {
                books.add(book);
            } else {
                System.out.println("Book with ISBN " + x + " not found.");
            }
        }
        
        return books;
    }

    public User getUserFromLoan(Loan loan) {
        return userService.getUserById(loan.getUserId());
    }

    public void updateLoanReturnedDate(int loanId) {
        try {
            loanDAO.updateLoanReturnedDate(loanId);
            System.out.println("Loan returned date updated successfully");
        } catch (SQLException e) {
            throw new ServiceException("Error updating loan returned date", e);
        }
    }

    public Loan getLoanById(int loanId) {
        try {
            return loanDAO.getLoanById(loanId);
        } catch (SQLException e) {
            throw new ServiceException("Error getting loan by id", e);
        }
    }
}
