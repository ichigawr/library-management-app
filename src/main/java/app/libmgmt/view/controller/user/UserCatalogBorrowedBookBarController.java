package app.libmgmt.view.controller.user;

import com.jfoenix.controls.JFXButton;

import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.EnumUtils;
import app.libmgmt.view.controller.admin.AdminBookViewDialogController;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

public class UserCatalogBorrowedBookBarController {

    @FXML
    private ImageView bookImage;

    @FXML
    private JFXButton bookReturnedButton;

    @FXML
    private Label borrowedDateLabel;

    @FXML
    private Label dueDateLabel;

    @FXML
    private HBox hBoxReturn;

    @FXML
    private Label nameLabel;

    @FXML
    private Label orderLabel;

    @FXML
    private ImageView returnImage;

    @FXML
    private Label returnLabel;

    @FXML
    private Pane returnPane;

    @FXML
    private Pane viewPane;

    @FXML
    private ImageView imageView;

    private String bookId;

    private final String hoverReturnedLogo = "/assets/icon/redo 1 (1).png";
    private final String returnedLogo = "/assets/icon/redo 1.png";
    private final String hoverViewLogo = "/assets/icon/Property 1=Variant2.png";
    private final String viewLogo = "/assets/icon/btn view.png";

    @FXML
    void btnBookReturnedOnAction(ActionEvent event) {
        openPopUp("/fxml/user/user-return-book-confirmation-dialog.fxml", EnumUtils.PopupList.RETURN_BOOK);
        UserCatalogController.getInstance().setDeletedOrderNumber(orderLabel.getText());
    }

    @FXML
    void btnBookReturnedOnMouseEntered(MouseEvent event) {
        hBoxReturn.setStyle(
                "-fx-background-color: #f2f2f2; -fx-background-radius: 10px; -fx-border-color: #000; -fx-border-radius: 10px; -fx-border-width: 1.2px;");
        returnImage.setImage(new Image(getClass().getResource(hoverReturnedLogo).toExternalForm()));
        returnLabel.setStyle("-fx-text-fill: #000;");
    }

    @FXML
    void btnBookReturnedOnMouseExited(MouseEvent event) {
        hBoxReturn.setStyle("-fx-background-color: #000; -fx-background-radius: 10px;");
        returnImage.setImage(new Image(getClass().getResource(returnedLogo).toExternalForm()));
        returnLabel.setStyle("-fx-text-fill: #f2f2f2;");
    }

    @FXML
    void imageViewOnMouseClicked(MouseEvent event) {
        openPopUp("/fxml/admin/admin-book-view-dialog.fxml", EnumUtils.PopupList.BOOK_VIEW);
        String[] bookData = UserGlobalController.getInstance().getBookDataById(bookId);
        AdminBookViewDialogController.getInstance().setData(bookData);
    }

    @FXML
    void imageViewOnMouseEntered(MouseEvent event) {
        imageView.setImage(new Image(getClass().getResource(hoverViewLogo).toExternalForm()));
    }

    @FXML
    void imageViewOnMouseExited(MouseEvent event) {
        imageView.setImage(new Image(getClass().getResource(viewLogo).toExternalForm()));
    }

    public void setData(String[] data) {
        // form data in global: [bookId, bookImage, bookName, borrowedDate, dueDate]
        // form data in book bar: [orderNumber, bookImage, bookName, borrowedDate,
        // dueDate]
        bookId = data[0];
        orderLabel.setText(Integer.toString(UserGlobalController.getInstance().getBorrowedBooksData().
            indexOf(data) + 1));
        if (data[1] != null) {
            updateImage(data[1], bookImage);
        }
        nameLabel.setText(data[2]);
        borrowedDateLabel.setText(data[3]);
        dueDateLabel.setText(data[4]);

    }

    private void updateImage(String imgPath, ImageView bookImage) {
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

    public void setVisibleAction(boolean isReturned) {
        returnPane.setVisible(isReturned);
        viewPane.setVisible(!isReturned);
    }

    public void setDisableReturnButton(boolean disable) {
        bookReturnedButton.setDisable(disable);
        returnPane.setOpacity(disable ? 0.3 : 1);
    }

    private void openPopUp(String fxmlPath, EnumUtils.PopupList popupType) {
        ChangeScene.openAdminPopUp(UserGlobalController.getInstance().getStackPaneContainer(),
                fxmlPath, bookId, popupType);
    }

    public void setOrderNumber(String orderNumber) {
        orderLabel.setText(orderNumber);
    }

}
