package app.libmgmt.view.controller.user;

import java.util.List;
import java.util.ArrayList;
import java.util.function.Consumer;

import app.libmgmt.model.Loan;
import app.libmgmt.model.User;
import app.libmgmt.model.Book;
import app.libmgmt.service.LoanService;
import app.libmgmt.service.BookService;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.view.controller.LoginController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

public class UserGlobalController {

    private static UserGlobalController controller;

    // Collections for data management
    private final ObservableList<Loan> borrowedBooksData;
    private final ObservableList<Loan> returnedBooksData;
    private final ObservableList<Book> observableBooksData = FXCollections.observableArrayList();

    private final LoanService loanService = new LoanService();
    private final BookService bookService = new BookService();

    // FXML injected components
    @FXML
    private Pane backgroundPane;
    @FXML
    private HBox globalFormContainer;
    @FXML
    private Pane pagingPane;
    @FXML
    private StackPane stackPaneContainer;

    private final User user;

    // Constructor and Singleton Pattern
    public UserGlobalController() {
        controller = this;
        user = LoginController.getInstance().getUserLoginInfo();

        // Initialize collections
        borrowedBooksData = FXCollections.observableArrayList(preLoadLoansData());
        returnedBooksData = FXCollections.observableArrayList(setOriginalReturnedBooksData());
    }

    public static UserGlobalController getInstance() {
        return controller;
    }

    @FXML
    public void initialize() {
        AnimationUtils.fadeInRight(pagingPane, 1);
    }

    // Data Pre-loading Methods
    public List<Loan> preLoadLoansData() {
        // test data
        // return loanService.getLoansByUserId("23020604");
        return loanService.getBorrowedLoansByUserId(user.getUserId());
    }

    public List<Loan> setOriginalReturnedBooksData() {
        // return loanService.getReturnLoansByUserId("23020604");
        return loanService.getReturnLoansByUserId(user.getUserId());
    }

    public List<Book> fetchBooksFromDatabase() {
        return bookService.getAllBooks();
    }

    public void preLoadBooksData(Consumer<List<Book>> onSuccess, Consumer<Throwable> onFailure) {
        Task<List<Book>> fetchBooksTask = new Task<>() {
            @Override
            protected List<Book> call() {
                return bookService.getAllBooks();
            }
        };
    
        fetchBooksTask.setOnSucceeded(event -> onSuccess.accept(fetchBooksTask.getValue()));
    
        fetchBooksTask.setOnFailed(event -> onFailure.accept(fetchBooksTask.getException()));
    
        new Thread(fetchBooksTask).start();
    }

    // CRUD Methods
    public void addBorrowedBook(List<Loan> newBorrowedBooksList) {
        if (newBorrowedBooksList == null || newBorrowedBooksList.isEmpty()) {
            return;
        }

        // Merge multiple books into one loan
        List<String> isbn = new ArrayList<>();
        List<String> amount = new ArrayList<>();
        for (int i = 0; i < newBorrowedBooksList.size(); i++) {
            Loan loan = newBorrowedBooksList.get(i);
            isbn.add(loan.getIsbn());
            amount.add(String.valueOf(loan.getAmount()));
        }

        String isbn_String = String.join(", ", isbn);
        String amount_String = String.join(", ", amount);   
        Loan new_loan = newBorrowedBooksList.get(0);
        new_loan.setAmount(amount_String);
        new_loan.setIsbn(isbn_String);
        loanService.addLoan(new_loan);
        borrowedBooksData.add(new_loan);

        // Update available copies
        for (int i = 0; i < observableBooksData.size(); i++) {
            Book book = observableBooksData.get(i);
            for (int j = 0; j < newBorrowedBooksList.size(); j++) {
                Loan loan = newBorrowedBooksList.get(j);
                if (book.getIsbn().equals(loan.getIsbn())) {
                    int loanAmount = Integer.parseInt(loan.getAmount());
                    bookService.updateAvailableCopies(book.getIsbn(), book.getAvailableCopies() - loanAmount);
                    book.setAvailableCopies(book.getAvailableCopies() - loanAmount);
                    break;
                }
            }
        }
    }

    public void addReturnedBook(int loanId) {
        int max = loanService.getMaxLoanId();
        if (loanId <= 0 || loanId > max) {
            System.out.println("Invalid loan id");
            return;
        }

        for (int i = 0; i < borrowedBooksData.size(); i++) {
            Loan data = borrowedBooksData.get(i);
            if (data.getLoanId() == loanId) {
                data.markAsReturned();
                returnedBooksData.add(data);
                break;
            }
        }
    }

    public Book getBookDataById(String id) {
        if (id == null || id.trim().isEmpty()) {
            return null;
        }

        return observableBooksData.stream()
                .filter(data -> data.getIsbn().equals(id))
                .findFirst()
                .orElse(null);
    }

    public boolean isBookReturned(int loanId, String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }
        for (int i = 0; i < borrowedBooksData.size(); i++) {
            Loan data = borrowedBooksData.get(i);
            if (data.getLoanId() == loanId && data.getIsbn().equals(isbn) && data.getStatus().equals("RETURNED")) {
                return true;
            }
        }

        return false;
    }

    public boolean isBookBorrowed(String isbn) {
        if (isbn == null || isbn.trim().isEmpty()) {
            return false;
        }

        for (int i = 0; i < borrowedBooksData.size(); i++) {
            Loan data = borrowedBooksData.get(i);
            if (data.getIsbn().equals(isbn) && (borrowedBooksData.get(i).getStatus().equals("BORROWED") || 
            borrowedBooksData.get(i).getStatus().equals("OVERDUE"))) {
                return true;
            }
        }

        return false;
    }

    // Getter Methods for Data
    public User getUserLoginInfo() {
        return user;
    }

    public ObservableList<Loan> getBorrowedBooksData() {
        return borrowedBooksData;
    }

    public ObservableList<Book> getObservableBooksData() {
        return observableBooksData;
    }

    public ObservableList<Book> getObservableBookData() {
        return observableBooksData;
    }

    public void setObservableBookData(List<Book> data) {
        observableBooksData.setAll(data);
    }

    public ObservableList<Loan> getReturnedBooksData() {
        return returnedBooksData;
    }

    // Getter Methods for UI components
    public Pane getPagingPane() {
        return pagingPane;
    }

    public Pane getBackgroundPane() {
        return backgroundPane;
    }

    public StackPane getStackPaneContainer() {
        return stackPaneContainer;
    }

    public HBox getGlobalFormContainer() {
        return globalFormContainer;
    }

}
