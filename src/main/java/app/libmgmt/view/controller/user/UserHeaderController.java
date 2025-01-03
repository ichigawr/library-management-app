package app.libmgmt.view.controller.user;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.util.Duration;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.DateUtils;
import app.libmgmt.util.EnumUtils;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class UserHeaderController {

    @FXML
    private Label dateLabel;

    @FXML
    private HBox containerSettingBox;

    @FXML
    private HBox containerUserBox;

    @FXML
    private Label nameUserLabel;

    @FXML
    private AnchorPane rootPane;

    @FXML
    private ImageView settingImage;

    @FXML
    private Label timeLabel;

    @FXML
    private Label userRoleLabel;

    private static UserHeaderController controller;

    public UserHeaderController() {
        controller = this;
    }

    public static UserHeaderController getInstance() {
        return controller;
    }

    @FXML
    public void initialize() {
        AnimationUtils.fadeInDown(rootPane);
        setDateAndTimeHeader();
        setInformation();
    }

    public static void setInformation() {
        getInstance().nameUserLabel.setText("Welcome, " + UserGlobalController.getInstance().getUserLoginInfo().getName());
        if (UserGlobalController.getInstance().getUserLoginInfo().getUserRole().equals("STUDENT")) {
            getInstance().userRoleLabel.setText("Student");
        } else {
            getInstance().userRoleLabel.setText("External Borrower");
        }
    }

    public void setDateAndTimeHeader() {
        DateTimeFormatter time = DateTimeFormatter.ofPattern("hh:mm");
        timeLabel.setText(
                time.format(java.time.LocalTime.now()) + " " + (LocalDateTime.now().getHour() < 12 ? "AM" : "PM"));

        Locale locale = DateUtils.locale;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        String date = dateFormat.format(new Date());
        dateLabel.setText(date);

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime currentTime = LocalDateTime.now();
            timeLabel.setText(time.format(java.time.LocalTime.now()) + " " + (currentTime.getHour() < 12
                    ? "AM"
                    : "PM"));

            String newDate = dateFormat.format(new Date());
            dateLabel.setText(newDate);
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    // Handles the click event on the settings icon
    @FXML
    private void handleSettingOnMouseClicked(MouseEvent event) {
        openSettingsDialog();
    }

    // Opens the settings dialog
    private void openSettingsDialog() {
        ChangeScene.openAdminPopUp(
                UserGlobalController.getInstance().getStackPaneContainer(),
                "/fxml/change-credentials-dialog.fxml", EnumUtils.PopupList.CHANGE_CREDENTIALS);
    }

    @FXML
    void settingImageOnMouseEntered(MouseEvent event) {
        settingImage.setImage(new Image(getClass().getResource("/assets/icon/setting2.png").toExternalForm()));
    }

    @FXML
    void settingImageOnMouseExited(MouseEvent event) {
        settingImage.setImage(new Image(getClass().getResource("/assets/icon/setting1.png").toExternalForm()));
    }

}
