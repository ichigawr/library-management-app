package view;

import animatefx.animation.ZoomIn;
import animatefx.animation.ZoomOut;
import com.jfoenix.controls.JFXButton;
import initialize.AdminInitializer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import util.AnimationUtils;
import util.ChangeScene;
import util.DateTimeUtils;
import view.admin.AdminGlobalFormController;

import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class HeaderController {
    private static int initializeTimes;

    @FXML
    private Text dateText;
    @FXML
    private Text nameUserText;
    @FXML
    private AnchorPane rootPane;
    @FXML
    private ImageView settingImage;
    @FXML
    private Text timeText;
    @FXML
    private Text typeUserText;

    @FXML
    public void initialize() {
        ++initializeTimes;

        showAnimation();

        nameUserText.setText("Lionel Ronaldo");
        typeUserText.setText("Admin");

        setDateAndTimeHeader();
    }

    // Set current date and time in the header
    public void setDateAndTimeHeader() {
        DateTimeFormatter time = DateTimeFormatter.ofPattern("hh:mm");
        timeText.setText(time.format(java.time.LocalTime.now()) + " " + (LocalDateTime.now().getHour() < 12 ? "AM" : "PM"));

        Locale locale = DateTimeUtils.locale;
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        String date = dateFormat.format(new Date());
        dateText.setText(date);

        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e -> {
            LocalDateTime currentTime = LocalDateTime.now();
            timeText.setText(time.format(java.time.LocalTime.now()) + " " + (currentTime.getHour() < 12 ? "AM" : "PM"));

            String newDate = dateFormat.format(new Date());
            dateText.setText(newDate);
        }), new KeyFrame(Duration.seconds(1)));

        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
    }

    @FXML
    void handleSettingOnMouseClicked(MouseEvent event) {
        ChangeScene.openAdminPopUp(AdminGlobalFormController.getInstance().
                getStackPaneContainer(), "/fxml/change-credentials-dialog.fxml");
    }

    public void showAnimation() {
        if (initializeTimes == 1) {
            AnimationUtils.fadeInDown(rootPane);
        }
    }
}
