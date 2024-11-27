package app.libmgmt.view.controller.admin;

import com.jfoenix.controls.JFXButton;
import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import app.libmgmt.model.Book;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;

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
    private Pane refreshPaneButton;
    @FXML
    private Pane searchPane;
    @FXML
    private TextField textSearch;
    @FXML
    private VBox vBoxBooksList;

    private String deletedOrderNumber;

    private final List<Book> observableBooksData = adminGlobalController.getObservableBookData();

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
        System.out.println("Admin Books Layout initialized");

        preloadData(observableBooksData);
        stackPaneContainer.setOnMouseClicked(event -> stackPaneContainer.requestFocus());
        listenBookDataChanges();
    }

    // Data Preloading
    public void preloadData(List<Book> data) {
        Task<Void> preloadTask = new Task<>() {
            @Override
            protected Void call() {
                try {
                    for (Book d : data) {
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
                        String publishedDateStr = (d.getPublishedDate() != null) ?
                                outputFormat.format(d.getPublishedDate()) : "Not Available";
                        String authorsString = String.join(", ", d.getAuthors());
                        String categoriesString = String.join(", ", d.getCategories());
    
                        String[] bookData = new String[]{
                            d.getIsbn(),
                            d.getCoverUrl(),
                            d.getTitle(),
                            categoriesString,
                            authorsString,
                            String.valueOf(d.getAvailableCopies()),
                            d.getPublisher(),
                            publishedDateStr
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
                AnimationUtils.zoomIn(scene, 1.0);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Book Data Changes Listener
    private void listenBookDataChanges() {
        adminGlobalController.getObservableBookData()
                .addListener((ListChangeListener.Change<? extends Book> change) -> {
                    while (change.next()) {
                        if (change.wasRemoved() && change.getRemovedSize() != change.getAddedSize()) {
                            Book book = change.getRemoved().get(0);
                            if (book != null) {
                                String bookId = book.getIsbn();
                                removeBookFromVBox(bookId);
                            }
                        }
                    }
                });
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
        Task<List<Book>> reloadTask = new Task<>() {
            @Override
            protected List<Book> call() {
                return adminGlobalController.fetchBooksFromDatabase();
            }

            @Override
            protected void succeeded() {
                observableBooksData.clear();
                observableBooksData.addAll(getValue());
                vBoxBooksList.getChildren().clear();
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
    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String searchText = textSearch.getText();
        if (!searchText.isEmpty()) {
            showFilteredData(searchText);
        }
    }

    private void showFilteredData(String searchText) {
        vBoxBooksList.getChildren().clear();
        adminGlobalController.getObservableBookData().stream()
                .filter(book -> book.getTitle().toLowerCase().contains(searchText.toLowerCase()))
                .forEach(book -> {
                    String publishedDateStr = (book.getPublishedDate() != null) 
                            ? book.getPublishedDate().toString() 
                            : "Not Available";
                    String authorsString = String.join(", ", book.getAuthors());
                    String categoriesString = String.join(", ", book.getCategories());
                    String[] bookData = new String[]{
                        book.getIsbn(),
                        book.getCoverUrl(),
                        book.getTitle(),
                        categoriesString,
                        authorsString,
                        String.valueOf(book.getAvailableCopies()),
                        book.getPublisher(),
                        publishedDateStr
                    };
                    loadBookBar(bookData);
                });
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
        ChangeScene.openAdminPopUp(stackPaneContainer, "/fxml/admin/admin-add-book-dialog.fxml");
    }

    @FXML
    void btnRefreshTableOnMouseEntered(MouseEvent event) {
    }

    @FXML
    void btnRefreshTableOnMouseExited(MouseEvent event) {
    }

    public StackPane getStackPaneContainer() {
        return stackPaneContainer;
    }

    public void setDeletedOrderNumber(String deletedOrderNumber) {
        this.deletedOrderNumber = deletedOrderNumber;
    }
}
