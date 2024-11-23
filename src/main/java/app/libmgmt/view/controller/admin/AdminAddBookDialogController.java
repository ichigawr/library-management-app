package app.libmgmt.view.controller.admin;

import com.jfoenix.controls.JFXButton;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import app.libmgmt.model.Book;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.RegExPatterns;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AdminAddBookDialogController {

    @FXML
    private Pane container;
    @FXML
    private TextField txtAuthor;
    @FXML
    private TextField txtCoverURL;
    @FXML
    private TextField txtName;
    @FXML
    private DatePicker publishedDatePicker;
    @FXML
    private TextField txtPublisher;
    @FXML
    private Spinner<Integer> quantitySpinner;
    @FXML
    private TextField txtType;
    @FXML
    private Label notificationLabel;
    @FXML
    private JFXButton closeDialogButton;
    @FXML
    private ImageView imgClose;
    
    public void initialize() {
        System.out.println("Admin Add Book Dialog initialized");

        AnimationUtils.hoverCloseIcons(closeDialogButton, imgClose);
        quantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,
                1000, 1));
        quantitySpinner.getValueFactory().setValue(0);
        quantitySpinner.setPromptText("Quantity*");

        container.setOnMouseClicked(
                event -> {
                    container.requestFocus();
                });
    }

    @FXML
    void addButtonOnAction(ActionEvent event) throws IOException {
        if (checkValidInfo()) {
            String[] bookData = new String[] { txtCoverURL.getText(), txtName.getText(),
                    txtType.getText(), txtAuthor.getText(), quantitySpinner.getValue().toString(),
                    txtPublisher.getText(), publishedDatePicker.getValue().toString() };
            addBook(bookData);
        }
    }

    public void addBook(String[] bookData) {
        // data format: [coverURL, name, type, author, quantity, publisher, publishedDate]
        String[] newBook = new String[] { "0", bookData[0], bookData[1], bookData[2], bookData[3], bookData[4],
                bookData[5], bookData[6] };
        AdminGlobalController adminGlobalController = AdminGlobalController.getInstance();
        adminGlobalController.insertBooksData(newBook);
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                AdminBooksLayoutController controller = AdminBooksLayoutController.getInstance();
                if (controller.getSearchText().isEmpty()) {
                    List<Book> data = new ArrayList<>() {
                        {
                            // add id to the first index
                            // data format: [id, coverURL, name, type, author, quantity, publisher,
                            // publishedDate] to match the format of preloadData method in
                            // AdminBooksLayoutController
                            Book lastBook = AdminGlobalController.getInstance().getObservableBookData().getLast();
                            String[] book_Data = new String[] { lastBook.getIsbn(), lastBook.getCoverUrl(),
                                    lastBook.getTitle(), lastBook.getCategories().toString(),
                                    lastBook.getAuthors().toString(), String.valueOf(lastBook.getAvailableCopies()),
                                    lastBook.getPublisher(), lastBook.getPublishedDate().toString() };
                            lastBook = new Book(book_Data);
                            add(lastBook);
                            System.out.println("Book added: Fac diu vai lc" + lastBook.toString());
                        }
                    };
                    controller.preloadData(data);
                }
                return null;
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                notificationLabel.setText("Book added successfully." + bookData[7]);
                AnimationUtils.playNotificationTimeline(notificationLabel, 3, "#08a80d");
            }

            @Override
            protected void failed() {
                super.failed();
            }
        };

        new Thread(task).start();
        setDefault();
    }

    public boolean checkValidInfo() throws IOException {
        String url = txtCoverURL.getText();
        String nameBook = txtName.getText();
        String quantity = quantitySpinner.getValue().toString();

        if (nameBook.isEmpty() || quantity.isEmpty() || url.isEmpty() || publishedDatePicker.getValue() == null) {
            notificationLabel.setText("Please fill in mandatory fields.");
            AnimationUtils.playNotificationTimeline(notificationLabel, 3, "#ff0000");
            return false;
        }
        if (!RegExPatterns.bookCoverUrlPattern(url)) {
            notificationLabel.setText("URL is invalid or not an image link.");
            AnimationUtils.playNotificationTimeline(notificationLabel, 3, "#ff0000");
            return false;
        }
        return true;
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        setDefault();
    }

    @FXML
    void closeButtonOnAction(ActionEvent event) {
        ChangeScene.closePopUp();
    }

    public void setDefault() {
        txtCoverURL.setText("");
        txtName.setText("");
        txtType.setText("");
        txtAuthor.setText("");
        quantitySpinner.getValueFactory().setValue(0);
        publishedDatePicker.setValue(null);
        txtPublisher.setText("");
    }

}
