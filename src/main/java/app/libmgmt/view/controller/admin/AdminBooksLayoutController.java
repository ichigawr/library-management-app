package app.libmgmt.view.controller.admin;

import com.jfoenix.controls.JFXButton;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import app.libmgmt.model.Book;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.EnumUtils.PopupList;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;
import java.text.SimpleDateFormat;

public class AdminBooksLayoutController {

    private static AdminBooksLayoutController controller;
    private final AdminGlobalController adminGlobalController = AdminGlobalController.getInstance();

    @FXML
    private StackPane stackPaneContainer;
    @FXML
    private JFXButton addBookButton;
    @FXML
    private JFXButton apiButton;
    @FXML
    private JFXButton refreshButton;
    @FXML
    private Pane refreshPane;
    @FXML
    private Pane searchPane;
    @FXML
    private TextField textSearch;
    @FXML
    private VBox vBoxBooksList;
    @FXML
    private HBox hBoxAddBook;
    @FXML
    private ImageView addBookLogo;
    @FXML
    private Label addBookLabel;
    @FXML
    private Pane apiPane;
    @FXML
    private Label apiLabel;

    private ListChangeListener<Book> bookDataChangeListener;

    private String deletedOrderNumber;

    private final List<Book> observableBooksData = adminGlobalController.getObservableBookData();

    private Timeline debounceTimeline;

    private final String hoverAcquireLogo = "/assets/icon/acquire-logo-1.png";
    private final String acquireLogo = "/assets/icon/add-circle 1.png";

    public AdminBooksLayoutController() {
        controller = this;
    }

    public static AdminBooksLayoutController getInstance() {
        return controller;
    }

    // Initializer
    @FXML
    public void initialize() {
        Logger.getLogger("javafx").setLevel(java.util.logging.Level.SEVERE);

        debounceDataSearch();

        preloadData(observableBooksData);
        stackPaneContainer.setOnMouseClicked(event -> stackPaneContainer.requestFocus());
    }

    private void debounceDataSearch() {
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(280), e -> performSearch()));
        debounceTimeline.setCycleCount(1);

        textSearch.setOnKeyTyped(event -> {
            if (debounceTimeline.getStatus() == Timeline.Status.RUNNING) {
                debounceTimeline.stop();
            }
            debounceTimeline.play();
        });
    }

    // Data Preloading
    public void preloadData(List<Book> data) {
        Task<Void> preloadTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    for (Book d : data) {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String publishedDateStr = (d.getPublishedDate() != null)
                                ? outputFormat.format(d.getPublishedDate())
                                : "Not Available";
                        String authorsString = String.join(", ", d.getAuthors());
                        String categoriesString = String.join(", ", d.getCategories());

                        String[] bookData = new String[] {
                                d.getIsbn(),
                                d.getCoverUrl(),
                                d.getTitle(),
                                categoriesString,
                                authorsString,
                                String.valueOf(d.getAvailableCopies()),
                                d.getPublisher(),
                                publishedDateStr,
                                d.getWebReaderUrl() != null ? d.getWebReaderUrl() : "Not Available"
                        };

                        loadBookBar(bookData);
                    }
                } catch (Exception e) {
                    System.out.println("Error loading data table: " + e.getMessage());
                    throw new RuntimeException(e);
                }
                return null;
            }

            @Override
            protected void failed() {
                // Handle when task fails
                System.out.println("Task failed: " + getException().getMessage());
            }
        };

        new Thread(preloadTask).start();
    }

    private void loadBookBar(String[] data) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(
                    AdminBooksLayoutController.class.getResource("/fxml/admin/admin-book-bar.fxml"));
            Pane scene = fxmlLoader.load();
            scene.setId(data[0]);
            AdminBookBarController controller = fxmlLoader.getController();
            controller.setData(data);

            // Set the controller as UserData for easy access later
            scene.setUserData(controller);

            Platform.runLater(() -> {
                vBoxBooksList.getChildren().add(scene);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Book Data Changes Listener
    public void listenBookDataChanges() {
        bookDataChangeListener = change -> {
            while (change.next()) {
                if (change.wasRemoved() && change.getRemovedSize() != change.getAddedSize()) {
                    Book book = change.getRemoved().get(0);
                    if (book != null) {
                        String bookId = book.getIsbn();
                        removeBookFromVBox(bookId);
                    }
                }
            }
        };
    
        adminGlobalController.getObservableBookData().addListener(bookDataChangeListener);
    }
    
    public void stopListeningBookDataChanges() {
        if (bookDataChangeListener != null) {
            adminGlobalController.getObservableBookData().removeListener(bookDataChangeListener);
            bookDataChangeListener = null;
        }
    }

    private void removeBookFromVBox(String bookId) {
        vBoxBooksList.getChildren().stream()
                .filter(child -> child.getId() != null && child.getId().equals(bookId))
                .findFirst()
                .ifPresent(child -> Platform.runLater(() -> {
                    vBoxBooksList.getChildren().remove(child);
                    handleChangeOrderNumber(deletedOrderNumber);
                }));
    }

    private void handleChangeOrderNumber(String deletedOrderNumber) {
        // deletedOrderNumber = "1";
        if (deletedOrderNumber == null || deletedOrderNumber.isEmpty()) {
            deletedOrderNumber = "1";
        }
        int orderNumber = Integer.parseInt(deletedOrderNumber);
        for (int i = orderNumber - 1; i < vBoxBooksList.getChildren().size(); i++) {
            Pane bookBar = (Pane) vBoxBooksList.getChildren().get(i);
            AdminBookBarController controller = (AdminBookBarController) bookBar.getUserData();
            if (controller != null) {
                controller.setOrderNumber(String.valueOf(i + 1));
            } else {
                System.out.println("Controller property is missing or not of type AdminBookBarController");
            }
        }
    }

    // Refresh Table
    @FXML
    void btnRefreshTableOnAction(ActionEvent event) {
        refreshBooksList();
    }

    public void refreshBooksList() {
        vBoxBooksList.getChildren().clear();
        adminGlobalController.getObservableBookData().clear();
        Task<List<Book>> reloadTask = new Task<>() {
            @Override
            protected List<Book> call() {
                return adminGlobalController.fetchBooksFromDatabase();
            }

            @Override
            protected void succeeded() {
                adminGlobalController.getObservableBookData().addAll(getValue());
                preloadData(observableBooksData);
                textSearch.clear();
                textSearch.setEditable(true);
            }

            @Override
            protected void failed() {
                System.out.println("Failed to reload data from database: " + getException().getMessage());
            }
        };

        new Thread(reloadTask).start();
    }

    // Search Functionality
    private void performSearch() {
        vBoxBooksList.getChildren().clear();
        String searchText = textSearch.getText();
        if (!searchText.isEmpty()) {
            showFilteredData(searchText);
        } else {
            refreshBooksList();
        }
    }

    public void showFilteredData(String searchText) {
        String searchLower = searchText.toLowerCase().trim();

        // Split search terms by spaces for multi-criteria search
        String[] searchTerms = searchLower.split("\\s+");

        adminGlobalController.getObservableBookData().stream()
                .filter(book -> matchesAllSearchTerms(book, searchTerms))
                .forEach(book -> {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");
                    String publishedDateStr = (book.getPublishedDate() != null)
                            ? outputFormat.format(book.getPublishedDate())
                            : "Not Available";
                    String authorsString = String.join(", ", book.getAuthors());
                    String categoriesString = String.join(", ", book.getCategories());
                    String[] bookData = new String[] {
                            book.getIsbn(),
                            book.getCoverUrl(),
                            book.getTitle(),
                            categoriesString,
                            authorsString,
                            String.valueOf(book.getAvailableCopies()),
                            book.getPublisher(),
                            publishedDateStr,
                            book.getWebReaderUrl()
                    };
                    loadBookBar(bookData);
                });
    }

    private boolean matchesAllSearchTerms(Book book, String[] searchTerms) {
        return java.util.Arrays.stream(searchTerms)
                .allMatch(term -> matchesSingleTerm(book, term));
    }

    private boolean matchesSingleTerm(Book book, String searchTerm) {
        // Convert collections to lowercase strings for comparison
        String authorsLower = String.join(" ", book.getAuthors()).toLowerCase();
        String categoriesLower = String.join(" ", book.getCategories()).toLowerCase();

        return book.getIsbn().toLowerCase().contains(searchTerm) ||
                book.getTitle().toLowerCase().contains(searchTerm) ||
                authorsLower.contains(searchTerm) ||
                categoriesLower.contains(searchTerm) ||
                book.getPublisher().toLowerCase().contains(searchTerm);
    }

    @FXML
    void txtSearchOnMouseMoved(MouseEvent event) {
    }

    public String getSearchText() {
        return textSearch.getText();
    }

    // Button Actions
    @FXML
    void addBookButtonClicked(MouseEvent event) throws IOException {
        ChangeScene.openAdminPopUp(stackPaneContainer, "/fxml/admin/admin-add-book-dialog.fxml", PopupList.ADD_BOOK);
    }

    @FXML
    void apiButtonOnMouseClicked(MouseEvent event) {
        ChangeScene.openAdminPopUp(stackPaneContainer, "/fxml/admin/admin-add-book-api-dialog.fxml", PopupList.API);
    }

    @FXML
    void paneOnMouseEntered(MouseEvent event) {
        Object source = event.getSource();
        if (source.equals(searchPane)) {
            AnimationUtils.createScaleTransition(1.05, searchPane).play();
        } else if (source.equals(refreshButton)) {
            AnimationUtils.createScaleTransition(1.15, refreshPane).play();
        } else if (source.equals(apiButton)) {
            apiPane.setStyle(
                    "-fx-background-color: #F2F2F2; -fx-background-radius: 12px; -fx-border-color: #000; -fx-border-width: 0.5px; -fx-border-radius: 12px; -fx-background-insets: 0; -fx-border-insets: -1;");
            apiLabel.setStyle("-fx-text-fill: #000;");
            AnimationUtils.createScaleTransition(1.05, apiPane).play();
        }
    }

    @FXML
    void paneOnMouseExited(MouseEvent event) {
        Object source = event.getSource();
        if (source.equals(searchPane)) {
            AnimationUtils.createScaleTransition(AnimationUtils.DEFAULT_SCALE, searchPane).play();
        } else if (source.equals(refreshButton)) {
            AnimationUtils.createScaleTransition(AnimationUtils.DEFAULT_SCALE, refreshPane).play();
        } else if (source.equals(apiButton)) {
            apiPane.setStyle(
                    "-fx-background-color: #000; -fx-background-radius: 12px; -fx-border-color: #F2F2F2; -fx-border-width: 0.5px; -fx-border-radius: 12px; -fx-background-insets: 0; -fx-border-insets: -1;");
            apiLabel.setStyle("-fx-text-fill: #F2F2F2;");
            AnimationUtils.createScaleTransition(AnimationUtils.DEFAULT_SCALE, apiPane).play();
        }
    }

    @FXML
    void btnAddBookOnMouseEntered(MouseEvent event) {
        hBoxAddBook.setStyle(
                "-fx-background-color: #F2F2F2; -fx-background-radius: 12px; -fx-border-color: #000; -fx-border-width: 2px; -fx-border-radius: 12px; -fx-background-insets: 0; -fx-border-insets: -1;");
        addBookLogo.setImage(new Image(getClass().getResource(hoverAcquireLogo).toExternalForm()));
        addBookLabel.setStyle("-fx-text-fill: #000;");
        AnimationUtils.createScaleTransition(AnimationUtils.HOVER_SCALE, hBoxAddBook).play();
    }

    @FXML
    void btnAddBookOnMouseExited(MouseEvent event) {
        hBoxAddBook.setStyle(
                "-fx-background-color: #000; -fx-background-radius: 12px; -fx-border-color: #F2F2F2; -fx-border-width: 2px; -fx-border-radius: 12px; -fx-background-insets: 0; -fx-border-insets: -1;");
        addBookLogo.setImage(new Image(getClass().getResource(acquireLogo).toExternalForm()));
        addBookLabel.setStyle("-fx-text-fill: #F2F2F2;");
        AnimationUtils.createScaleTransition(AnimationUtils.DEFAULT_SCALE, hBoxAddBook).play();
    }

    public StackPane getStackPaneContainer() {
        return stackPaneContainer;
    }

    public void setDeletedOrderNumber(String deletedOrderNumber) {
        System.out.println("deletedOrderNumber: " + deletedOrderNumber);
        this.deletedOrderNumber = deletedOrderNumber;
    }
}
