package app.libmgmt.view.controller.admin;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.EnumUtils;

public class AdminUsersViewDialogController {

    private static AdminUsersViewDialogController controller;

    @FXML
    private JFXButton cancelButton;
    @FXML
    private JFXButton closeDialogButton;
    @FXML
    private Pane container;
    @FXML
    private ImageView imgClose;
    @FXML
    private Label emailLabel;
    @FXML
    private Label fullNameLabel;
    @FXML
    private Label idLabel;
    @FXML
    private Label majorOrPhoneLabel;
    @FXML
    private Label userTypeLabel;

    public AdminUsersViewDialogController() {
        controller = this;
    }

    public static AdminUsersViewDialogController getInstance() {
        return controller;
    }

    public void initialize() {
        System.out.println("AdminUserViewDialogController initialized");
        setupCloseIconAnimation();
    }

    @FXML
    private void cancelButtonOnAction(ActionEvent event) {
        closeDialog();
    }

    @FXML
    private void closeButtonOnAction(ActionEvent event) {
        closeDialog();
    }

    /**
     * Sets the user data in the view dialog.
     *
     * @param data     The array containing user details.
     * @param userType The type of the user (STUDENT or GUEST).
     */
    public void setData(String[] data, EnumUtils.UserType userType) {
        idLabel.setText("ID : " + data[3]);
        fullNameLabel.setText("Full name : " + data[0]);
        majorOrPhoneLabel
                .setText(userType == EnumUtils.UserType.STUDENT ? "Major : " + data[1] : "Phone number : " + data[1]);
        emailLabel.setText("Email : " + data[2]);
        if (userType == EnumUtils.UserType.STUDENT) {
            userTypeLabel.setText("Student");
        } else if (userType == EnumUtils.UserType.ADMIN) {
            userTypeLabel.setText("Admin");
        } else {
            userTypeLabel.setText("Guest");
        }
    }

    /**
     * Sets up hover animation for the close icon.
     */
    private void setupCloseIconAnimation() {
        AnimationUtils.hoverCloseIcons(closeDialogButton, imgClose);
    }

    /**
     * Closes the dialog popup.
     */
    private void closeDialog() {
        ChangeScene.closePopUp();
    }
}
