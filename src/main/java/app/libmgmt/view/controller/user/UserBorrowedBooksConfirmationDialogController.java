package app.libmgmt.view.controller.user;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import com.jfoenix.controls.JFXButton;

import app.libmgmt.model.Book;
import app.libmgmt.model.Loan;
import app.libmgmt.service.LoanService;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.DateUtils;
import app.libmgmt.util.EnumUtils;
import app.libmgmt.view.controller.admin.AdminBooksLayoutController;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.Date;

public class UserBorrowedBooksConfirmationDialogController {

    public enum BORROW_TYPE {
        NORMAL,
        REBORROW
    }

    public static BORROW_TYPE borrowType = BORROW_TYPE.NORMAL;

    private static UserBorrowedBooksConfirmationDialogController controller;

    @FXML
    private Label borrowedDateLabel;

    @FXML
    private JFXButton closeButton;

    @FXML
    private Label closeLabel;

    @FXML
    private Pane closePane;

    @FXML
    private JFXButton confirmButton;

    @FXML
    private Label confirmLabel;

    @FXML
    private Pane confirmPane;

    @FXML
    private Label totalBorrowedBooksLabel;

    @FXML
    private VBox vBoxSelectedBooksList;

    private List<Book> selectedBooksList;

    private List<Loan> newBorrowedBooksList;

    public UserBorrowedBooksConfirmationDialogController() {
        controller = this;
    }

    public static UserBorrowedBooksConfirmationDialogController getInstance() {
        return controller;
    }

    @FXML
    public void initialize() {
        if (borrowType == BORROW_TYPE.NORMAL) {
            selectedBooksList = UserBooksLayoutController.getInstance().getSelectedBooksList();
        } else if (borrowType == BORROW_TYPE.REBORROW) {
            selectedBooksList = UserReturnedBookViewDialogController.getInstance().getSelectedBooksList();
        }
        newBorrowedBooksList = new ArrayList<>();

        borrowedDateLabel.setText(DateUtils.parseLocalDateToString(DateUtils.currentLocalTime));
        totalBorrowedBooksLabel.setText(selectedBooksList.size() + (selectedBooksList.size() > 1 ? " Books" : " Book"));

        preloadData();
    }

    @FXML
    void btnCloseOnAction(ActionEvent event) {
        vBoxSelectedBooksList.getChildren().clear();
        closeButton.setDisable(true);
        closeLabel.setText("Closing...");
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.2), e -> {
            closeLabel.setText("Closed!");
            ChangeScene.closePopUp();
        }));
        timeline.play();
    }

    @FXML
    void btnConfirmOnAction(ActionEvent event) {
        confirmLabel.setText("Borrowing...");
        int idx = 0;
        for (javafx.scene.Node node : vBoxSelectedBooksList.getChildren()) {
            UserBorrowedBookBarController controller = (UserBorrowedBookBarController) node.getUserData();
            if (controller != null) {
                int amount = controller.getAmount();
                newBorrowedBooksList.get(idx++).setAmount(String.valueOf(amount));
            }
        }
        UserGlobalController.getInstance().addBorrowedBook(newBorrowedBooksList);
        confirmButton.setDisable(true);
        closeDialogAndNavigateToCatalog();
    }

    private void closeDialogAndNavigateToCatalog() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(0.5), e -> {
            confirmLabel.setText("Borrowed!");
        }));
        Timeline timeline2 = new Timeline(new KeyFrame(Duration.seconds(1.5), e -> {
            ChangeScene.closePopUp();
            if (borrowType == BORROW_TYPE.NORMAL) {
                UserCatalogController.currentStateUserCatalog = UserCatalogController.USER_CATALOG_STATE.BORROWED;
                try {
                    UserNavigationController userNavigationController = UserNavigationController.getInstance();
                    userNavigationController.handleNavigation(EnumUtils.NavigationButton.CATALOG, "user-catalog-form.fxml",
                            userNavigationController.getCatalogButton());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            } else if (borrowType == BORROW_TYPE.REBORROW) {
                ChangeScene.closePopUp();
                UserCatalogController.getInstance().handleChangeBorrowedBooksButtonOnAction();
                UserCatalogController.currentStateUserCatalog = UserCatalogController.USER_CATALOG_STATE.BORROWED;
            }
        }));
        timeline.play();
        timeline2.play();
    }

    @FXML
    void btnOnMouseEntered(MouseEvent event) {
        if (event.getSource() == closeButton) {
            closePane.setStyle(
                    "-fx-background-color: #d7d7d7; -fx-background-radius: 10;");
        } else if (event.getSource() == confirmButton) {
            confirmPane.setStyle(
                    "-fx-background-color: #F2F2F2; -fx-background-radius: 10; -fx-border-color: #000; -fx-border-radius: 10; -fx-border-width: 1.2;");
            confirmLabel.setStyle("-fx-text-fill: #000;");
        }
    }

    @FXML
    void btnOnMouseExited(MouseEvent event) {
        if (event.getSource() == closeButton) {
            closePane.setStyle(
                    "-fx-background-color: #fff; -fx-background-radius: 10;");
        } else if (event.getSource() == confirmButton) {
            confirmPane.setStyle(
                    "-fx-background-color: #000; -fx-background-radius: 10;");
            confirmLabel.setStyle("-fx-text-fill: #fff;");
        }

    }

    // Loads data asynchronously to prevent blocking the main thread.
    private void preloadData() {
        Task<Void> preloadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                int orderNumber = 1;
                for (Book d : selectedBooksList) {
                    loadBookData(d, orderNumber++);
                }
                return null;
            }

            @Override
            protected void failed() {
                System.err.println("Error loading data: " + getException().getMessage());
                throw new RuntimeException(getException());
            }
        };
        new Thread(preloadTask).start();
    }

    /**
     * Loads a single book's data into the VBox.
     * 
     * @param bookData Array containing [imageURL, title, author, date].
     */
    private void loadBookData(Book bookData, int orderNumber) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(AdminBooksLayoutController.class.getResource(
                    "/fxml/user/user-borrowed-book-bar.fxml"));
            Pane scene = fxmlLoader.load();
            UserBorrowedBookBarController controller = fxmlLoader.getController();
            controller.setOrderNumber(orderNumber);
            controller.setData(bookData);
            scene.setUserData(controller);

            setNewBorrowedBookData(bookData, orderNumber);

            Platform.runLater(() -> {
                vBoxSelectedBooksList.getChildren().add(scene);
                AnimationUtils.zoomIn(scene, 1.0);
            });
        } catch (Exception e) {
            System.err.println("Error loading book data: " + e.getMessage());
        }
    }

    public void setNewBorrowedBookData(Book bookData, int orderNumber) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

        String borrowedDateText = borrowedDateLabel.getText();
        try {
            Date parsedDate = inputFormat.parse(borrowedDateText);
            String formattedDate = outputFormat.format(parsedDate);

            LoanService loanService = new LoanService();
            Loan newBorrowedBookData = new Loan(
                    loanService.getMaxLoanId() + orderNumber,
                    UserGlobalController.getInstance().getUserLoginInfo().getUserId(),
                    bookData.getIsbn(),
                    // temporary amount value
                    "1",
                    DateUtils.parseStringToDate(formattedDate),
                    "BORROWED");
            newBorrowedBooksList.add(newBorrowedBookData);
        } catch (Exception e) {
            System.err.println("Error setting new borrowed book data: " + e.getMessage());
        }
    }
}
