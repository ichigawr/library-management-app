package app.libmgmt.view.controller.admin;

import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import java.io.IOException;

import com.google.zxing.WriterException;

import app.libmgmt.model.Book;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.EnumUtils;

public class AdminBookBarController {

    private static AdminBookBarController controller;

    @FXML
    private Label orderLabel, authorLabel, nameLabel, statusLabel, typeLabel;
    @FXML
    private ImageView bookImage, editFunction, deleteFunction, viewFunction;

    private int quantity = -1;
    private String imgPath = "", publisher = "", publishedDate = "", bookID = "", webReaderUrl = "";

    public AdminBookBarController() {
        controller = this;
    }

    public static AdminBookBarController getInstance() {
        return controller;
    }

    @FXML
    public void initialize() {
        AdminGlobalController.getInstance().getObservableBookData().addListener((ListChangeListener<Book>) change -> {
            while (change.next()) {
                if (change.wasReplaced() && change.getFrom() >= 0 && change.getFrom() < change.getList().size()) {
                    Book updatedBook = change.getList().get(change.getFrom());

                    String authorsString = String.join(", ", updatedBook.getAuthors());
                    String categoriesString = String.join(", ", updatedBook.getCategories());

                    String[] updatedBookData = new String[]{
                        updatedBook.getIsbn(),
                        updatedBook.getCoverUrl(),
                        updatedBook.getTitle(),
                        categoriesString,
                        authorsString,
                        String.valueOf(updatedBook.getAvailableCopies()),
                        updatedBook.getPublisher(),
                        updatedBook.getPublishedDate().toString()
                    };

                    if (bookID.equals(updatedBookData[0])) {
                        setData(updatedBookData);
                    }
                }
            }
        });
    }

    // --- Event Handlers ---
    @FXML
    void imgViewOnMouseClicked(MouseEvent event) throws WriterException, IOException {
        openPopUp("/fxml/admin/admin-book-view-dialog.fxml", EnumUtils.PopupList.BOOK_VIEW);
        AdminBookViewDialogController.getInstance().setData(getData());
    }

    @FXML
    void imgEditOnMouseClicked(MouseEvent event) {
        openPopUp("/fxml/admin/admin-book-edit-dialog.fxml", EnumUtils.PopupList.BOOK_EDIT);
        AdminBookEditDialogController.getInstance().showOriginalBookData(getData());
    }

    @FXML
    void imgDeleteOnMouseClicked(MouseEvent event) {
        openPopUp("/fxml/admin/admin-delete-confirmation-dialog.fxml", EnumUtils.PopupList.BOOK_DELETE);
        AdminBooksLayoutController.getInstance().setDeletedOrderNumber(orderLabel.getText());
    }

    @FXML
    void imgOnMouseEntered(MouseEvent event) {
        updateIconOnHover(event, true);
    }

    @FXML
    void imgOnMouseExited(MouseEvent event) {
        updateIconOnHover(event, false);
    }

    // --- Helper Methods ---
    private void openPopUp(String fxmlPath, EnumUtils.PopupList popupType) {
        ChangeScene.openAdminPopUp(AdminBooksLayoutController.getInstance().getStackPaneContainer(),
                fxmlPath, bookID, popupType);
    }

    private void updateIconOnHover(MouseEvent event, boolean isHovered) {
        ImageView source = (ImageView) event.getSource();
        String iconPath = getIconPath(source, isHovered);
        source.setImage(new Image(getClass().getResource(iconPath).toExternalForm()));
    }

    private String getIconPath(ImageView source, boolean isHovered) {
        if (source == viewFunction) {
            return isHovered ? "/assets/icon/Property 1=Variant2.png" : "/assets/icon/btn view.png";
        } else if (source == editFunction) {
            return isHovered ? "/assets/icon/edit2.png" : "/assets/icon/btn edit.png";
        } else if (source == deleteFunction) {
            return isHovered ? "/assets/icon/red-recycle.png" : "/assets/icon/btn Delete.png";
        }

        return "";
    }

    public String[] getData() {
        return new String[]{bookID, imgPath, nameLabel.getText(), typeLabel.getText(),
                authorLabel.getText(), Integer.toString(quantity), publisher, publishedDate, webReaderUrl};
    }

    public void setData(String[] data) {
        bookID = data[0];
        int bookIndex = -1;
        for (int i = 0; i < AdminGlobalController.getInstance().getObservableBookData().size(); i++) {
            if (AdminGlobalController.getInstance().getObservableBookData().get(i).getIsbn().equals(data[0])) {
                bookIndex = i;
                break;
            }
        }
        orderLabel.setText(Integer.toString(bookIndex + 1));
        updateImageIfChanged(data[1], bookImage);
        updateLabelIfChanged(nameLabel, data[2]);
        updateLabelIfChanged(typeLabel, data[3]);
        updateLabelIfChanged(authorLabel, data[4]);
        updateQuantityAndStatus(data[5]);

        if (!publisher.equals(data[6])) {
            publisher = data[6];
        }

        if (!publishedDate.equals(data[7])) {
            publishedDate = data[7];
        }

        if (!webReaderUrl.equals(data[8])) {
            webReaderUrl = data[8];
        }
    }

    private void updateLabelIfChanged(Label label, String newText) {
        if (!label.getText().equals(newText)) {
            label.setText(newText);
        }
    }

    /**
     * Updates the image if the path has changed. Uses a placeholder image initially,
     * and loads the new image asynchronously if needed.
     */
    private void updateImageIfChanged(String newImagePath, ImageView bookImage) {
        if (!imgPath.equals(newImagePath)) {
            imgPath = newImagePath;

            // Load the image asynchronously
            Task<Image> imageLoadTask = new Task<>() {
                @Override
                protected Image call() {
                    Image image = new Image(imgPath);
                    return image;
                }
            };

            imageLoadTask.setOnSucceeded(event -> bookImage.setImage(imageLoadTask.getValue()));
            imageLoadTask.setOnFailed(event -> System.out.println("Failed to load image: " + imgPath));

            new Thread(imageLoadTask).start();
        }
    }

    private void updateQuantityAndStatus(String newQuantityStr) {
        int newQuantity = Integer.parseInt(newQuantityStr);

        if (quantity != newQuantity) {
            quantity = newQuantity;
            statusLabel.setText(quantity >= 1 ? "Available" : "Borrowed");
            statusLabel.setStyle(quantity >= 1 ? "-fx-text-fill: green" : "-fx-text-fill: red");
        }
    }

    public String getId() {
        return bookID;
    }

    public void setOrderNumber(String order) {
        orderLabel.setText(order);
    }
}
