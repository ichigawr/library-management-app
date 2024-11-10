package view.admin;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import util.AnimationUtils;
import util.ChangeScene;
import util.EnumUtils;

import java.io.IOException;
import java.util.logging.Logger;

public class AdminNavigationController {

    private static EnumUtils.NavigationButton latestButtonClicked = EnumUtils.NavigationButton.DASHBOARD;
    private static int initializedTimes = 0;
    private static AdminNavigationController controller;
    @FXML
    private JFXButton booksButton;
    @FXML
    private ImageView booksLogo;
    @FXML
    private JFXButton catalogButton;
    @FXML
    private ImageView catalogLogo;
    @FXML
    private JFXButton dashboardButton;
    @FXML
    private ImageView dashboardLogo;
    @FXML
    private JFXButton logoutButton;
    @FXML
    private ImageView logoutLogo;
    @FXML
    private JFXButton usersButton;
    @FXML
    private ImageView usersLogo;
    @FXML
    private VBox navigationContainer;

    public AdminNavigationController() {
        controller = this;
    }

    public static AdminNavigationController getInstance() {
        return controller;
    }

    @FXML
    public void initialize() {
        Logger.getLogger("javafx").setLevel(java.util.logging.Level.SEVERE);
        System.out.println("Navigation Controller initialized " + ++initializedTimes + " times");

        showAnimation();
    }

    public void changeButtonStyle() {
        switch (latestButtonClicked) {
            case EnumUtils.NavigationButton.DASHBOARD:
                setStyle("/assets/icon/dashboard-icon-2.png", dashboardButton, dashboardLogo);
                break;
            case EnumUtils.NavigationButton.CATALOG:
                setStyle("/assets/icon/catalog-icon-2.png", catalogButton, catalogLogo);
                break;
            case EnumUtils.NavigationButton.BOOKS:
                setStyle("/assets/icon/books-icon-2.png", booksButton, booksLogo);
                break;
            case EnumUtils.NavigationButton.USERS:
                setStyle("/assets/icon/people-icon-2.png", usersButton, usersLogo);
                break;
        }
    }

    public void setStyle(String pathToLogo, JFXButton button, ImageView logo) {
        Image image = new Image(getClass().getResource(pathToLogo).toExternalForm());
        button.setStyle("-fx-background-color: white; -fx-text-fill: black;");
        logo.setImage(image);
    }

    public void setDefaultButtons() {
        Image defaultDashboardLogo = new Image(getClass().getResource("/assets/icon/dashboard-icon.png").toExternalForm());
        Image defaultBooksLogo = new Image(getClass().getResource("/assets/icon/books-icon.png").toExternalForm());
        Image defaultCatalogLogo = new Image(getClass().getResource("/assets/icon/catalog-icon.png").toExternalForm());
        Image defaultUsersLogo = new Image(getClass().getResource("/assets/icon/people-icon.png").toExternalForm());
        Image defaultLogOutLogo = new Image(getClass().getResource("/assets/icon/logout-icon.png").toExternalForm());

        dashboardLogo.setImage(defaultDashboardLogo);
        booksLogo.setImage(defaultBooksLogo);
        catalogLogo.setImage(defaultCatalogLogo);
        usersLogo.setImage(defaultUsersLogo);
        logoutLogo.setImage(defaultLogOutLogo);

        dashboardButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        booksButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        catalogButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        usersButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
        logoutButton.setStyle("-fx-background-color: black; -fx-text-fill: white;");
    }

    public void showAnimation() {
        if (initializedTimes == 1) {
            AnimationUtils.fadeInLeft(navigationContainer);
        }
    }

    public void handleEffectButtonClicked(JFXButton button) {
        latestButtonClicked = getButtonType(button);
        setDefaultButtons();
        changeButtonStyle();
    }

    public EnumUtils.NavigationButton getButtonType(JFXButton button) {
        if (button.equals(dashboardButton)) {
            return EnumUtils.NavigationButton.DASHBOARD;
        } else if (button.equals(catalogButton)) {
            return EnumUtils.NavigationButton.CATALOG;
        } else if (button.equals(booksButton)) {
            return EnumUtils.NavigationButton.BOOKS;
        } else if (button.equals(usersButton)) {
            return EnumUtils.NavigationButton.USERS;
        } else if (button.equals(logoutButton)) {
            return EnumUtils.NavigationButton.LOGOUT;
        }
        return null;
    }

    @FXML
    public void dashboardButtonClicked(MouseEvent event) throws IOException {
        if (latestButtonClicked == EnumUtils.NavigationButton.DASHBOARD) {
            return;
        }
        ChangeScene.navigateToScene("admin-dashboard.fxml");
        handleEffectButtonClicked(dashboardButton);
    }

    @FXML
    public void catalogButtonClicked(MouseEvent event) throws IOException {
        if (latestButtonClicked == EnumUtils.NavigationButton.CATALOG) {
            return;
        }
        ChangeScene.navigateToScene("admin-borrowed-books-form.fxml");
        handleEffectButtonClicked(catalogButton);
    }

    @FXML
    public void booksButtonClicked(MouseEvent event) throws IOException {
        if (latestButtonClicked == EnumUtils.NavigationButton.BOOKS) {
            return;
        }
        ChangeScene.navigateToScene("admin-books-form.fxml");
        handleEffectButtonClicked(booksButton);
    }

    @FXML
    public void userButtonClicked(MouseEvent event) throws IOException {
        if (latestButtonClicked == EnumUtils.NavigationButton.USERS) {
            return;
        }
        ChangeScene.navigateToScene("admin-users-form.fxml");
        handleEffectButtonClicked(usersButton);
    }

    @FXML
    public void logOutButtonClicked(MouseEvent event) throws IOException {
        ChangeScene.openAdminPopUp(AdminGlobalFormController.getInstance().getStackPaneContainer(),
                "/fxml/logout-dialog.fxml");
        handleEffectButtonClicked(logoutButton);
    }

}
