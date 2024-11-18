package app.libmgmt.view.controller;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;

public class ChangeCredentialsDialogController {

    @FXML
    private TextField curPasswordField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private TextField cfNewPasswordField;
    @FXML
    private JFXButton closeDialogButton;
    @FXML
    private ImageView imgClose;
    @FXML
    private Label notificationLabel;
    @FXML
    private Pane container;

    public void initialize() {
        System.out.println("ChangeCredentialsDialogController initialized");
        setupCloseIconAnimation();
        setupContainerClickFocus();
    }

    @FXML
    private void addButtonOnAction(ActionEvent event) {
        handleCredentialsChange("password");
    }

    /**
     * Handles the credentials change logic, validating input and providing feedback.
     *
     * @param correctCurrentPassword The correct current password for validation.
     */
    private void handleCredentialsChange(String correctCurrentPassword) {
        String curPassword = curPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String cfNewPassword = cfNewPasswordField.getText();

        if (areFieldsEmpty(curPassword, newPassword, cfNewPassword)) {
            displayNotification("Please fill all fields", "red");
        } else if (!isCurrentPasswordCorrect(curPassword, correctCurrentPassword)) {
            displayNotification("Current password is incorrect", "red");
        } else if (!doNewPasswordsMatch(newPassword, cfNewPassword)) {
            displayNotification("New passwords do not match", "red");
        } else {
            displayNotification("Credentials changed successfully", "green");
            setNewPassword(newPassword);
        }
    }

    @FXML
    private void cancelButtonOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    private void closeButtonOnAction(ActionEvent event) {
        ChangeScene.closePopUp();
    }

    /**
     * Sets up hover animation for the close button.
     */
    private void setupCloseIconAnimation() {
        AnimationUtils.hoverCloseIcons(closeDialogButton, imgClose);
    }

    /**
     * Sets up a mouse click handler on the container to remove focus from any field.
     */
    private void setupContainerClickFocus() {
        container.setOnMouseClicked(event -> container.requestFocus());
    }

    /**
     * Checks if any of the input fields are empty.
     *
     * @param curPassword  Current password input.
     * @param newPassword  New password input.
     * @param cfNewPassword Confirm new password input.
     * @return true if any field is empty, false otherwise.
     */
    private boolean areFieldsEmpty(String curPassword, String newPassword, String cfNewPassword) {
        return curPassword.isEmpty() || newPassword.isEmpty() || cfNewPassword.isEmpty();
    }

    /**
     * Validates the current password.
     *
     * @param curPassword The entered current password.
     * @param correctCurrentPassword The correct current password for comparison.
     * @return true if the password is correct, false otherwise.
     */
    private boolean isCurrentPasswordCorrect(String curPassword, String correctCurrentPassword) {
        return curPassword.equals(correctCurrentPassword);
    }

    /**
     * Checks if the new passwords match.
     *
     * @param newPassword The new password.
     * @param cfNewPassword The confirmation of the new password.
     * @return true if passwords match, false otherwise.
     */
    private boolean doNewPasswordsMatch(String newPassword, String cfNewPassword) {
        return newPassword.equals(cfNewPassword);
    }

    /**
     * Displays a notification with the specified message and color.
     *
     * @param message The message to display.
     * @param color The color of the notification text.
     */
    private void displayNotification(String message, String color) {
        notificationLabel.setText(message);
        AnimationUtils.playNotificationTimeline(notificationLabel, 3, color);
    }

    /**
     * Clears all password fields.
     */
    private void clearFields() {
        curPasswordField.clear();
        newPasswordField.clear();
        cfNewPasswordField.clear();
    }

    /**
     * Sets the new password. This method needs to be implemented.
     *
     * @param newPassword The new password to set.
     */
    private void setNewPassword(String newPassword) {
        // TODO: Implement the actual password change logic.
    }
}