package app.libmgmt.view.controller.admin;

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
import app.libmgmt.model.ExternalBorrower;
import app.libmgmt.model.Student;
import app.libmgmt.model.User;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.EnumUtils;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import com.jfoenix.controls.JFXButton;

public class AdminUsersLayoutController {

    private static AdminUsersLayoutController controller;
    private final AdminGlobalController adminGlobalController = AdminGlobalController.getInstance();
    private final List<Student> studentsData = adminGlobalController.getObservableStudentsData();
    private final List<ExternalBorrower> guestsData = adminGlobalController.getObservableExternalBorrowersData();
    @FXML
    public StackPane stackPaneContainer;
    @FXML
    private HBox hBoxGuest;
    @FXML
    private HBox hBoxStudent;
    @FXML
    private Pane hBoxAddUser;
    @FXML
    private Pane studentPane;
    @FXML
    private Label studentLabel;
    @FXML
    private Pane guestPane;
    @FXML
    private Pane searchPane;
    @FXML
    private Pane refreshPaneButton;
    @FXML
    private Label guestLabel;
    @FXML
    private VBox vBoxUserList;
    @FXML
    private TextField textSearch;
    @FXML
    private JFXButton studentButton;
    @FXML
    private JFXButton guestButton;
    @FXML
    private JFXButton addUserButton;
    @FXML
    private JFXButton refreshButton;
    @FXML
    private Label addLabel;
    @FXML
    private ImageView addImage;

    private Timeline debounceTimeline;

    private final String hoverAcquireLogo = "/assets/icon/acquire-logo-1.png";
    private final String acquireLogo = "/assets/icon/add-circle 1.png";

    private EnumUtils.UserType status = EnumUtils.UserType.STUDENT;

    public AdminUsersLayoutController() {
        controller = this;
    }

    public static AdminUsersLayoutController getInstance() {
        return controller;
    }

    @FXML
    public void initialize() {
        Logger.getLogger("javafx").setLevel(java.util.logging.Level.SEVERE);
        System.out.println("Initialize Catalog Layout");

        debounceDataSearch();

        setVisibility(true, false);
        showStudentsList();

        // Listen for changes in user data for both students and guests
        listenUserListChange(EnumUtils.UserType.STUDENT);
        listenUserListChange(EnumUtils.UserType.GUEST);
    }

        // Debounce Search
    private void debounceDataSearch() {
        debounceTimeline = new Timeline(new KeyFrame(Duration.millis(280), event -> {
            performSearch();
        }));
        debounceTimeline.setCycleCount(1);

        textSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            if (debounceTimeline.getStatus() == Timeline.Status.RUNNING) {
                debounceTimeline.stop();
            }
            debounceTimeline.play();
        });
    }

    private void listenUserListChange(EnumUtils.UserType userType) {
        switch (userType) {
            case STUDENT:
                adminGlobalController.getObservableStudentsData()
                        .addListener((ListChangeListener.Change<? extends Student> change) -> {
                            while (change.next()) {
                                boolean isUpdate = change.wasRemoved()
                                        && change.getRemovedSize() == change.getAddedSize();

                                if (change.wasRemoved() && !isUpdate) {
                                    System.out.println("Student removed");

                                    for (Student removeUser : change.getRemoved()) {
                                        String userId = removeUser.getStudentId();
                                        removeUserFromVBox(userId);
                                    }
                                }
                            }
                        });
                break;
            case GUEST:
                adminGlobalController.getObservableExternalBorrowersData()
                        .addListener((ListChangeListener.Change<? extends ExternalBorrower> change) -> {
                            while (change.next()) {
                                boolean isUpdate = change.wasRemoved()
                                        && change.getRemovedSize() == change.getAddedSize();

                                if (change.wasRemoved() && !isUpdate) {
                                    System.out.println("Guest removed");

                                    for (ExternalBorrower removeUser : change.getRemoved()) {
                                        String userId = removeUser.getSocialId();
                                        removeUserFromVBox(userId);
                                    }
                                }
                            }
                        });
                break;
            default:
                break;
        }
    }

    public void setVisibility(boolean visibilityStudent, boolean visibilityGuest) {
        hBoxStudent.setVisible(visibilityStudent);
        hBoxGuest.setVisible(visibilityGuest);
    }

    public void preloadExternalBorrowerData(List<ExternalBorrower> allUsersData, String path, PreloadType preloadType) {
        if (!vBoxUserList.getChildren().isEmpty() && preloadType == PreloadType.RESET) {
            vBoxUserList.getChildren().clear();
        }

        Task<Void> preloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (ExternalBorrower externalBorrower : allUsersData) {
                    try {
                        String[] externalBorrowerData = new String[] {
                                externalBorrower.getUserRole(),
                                externalBorrower.getName(),
                                externalBorrower.getPhoneNumber(),
                                externalBorrower.getEmail(),
                                externalBorrower.getSocialId()
                        };

                        Pane scene = loadScene(path, externalBorrowerData);
                        scene.setId(externalBorrower.getSocialId());

                        Platform.runLater(() -> vBoxUserList.getChildren().add(scene));
                    } catch (IOException e) {
                        throw new RuntimeException("Error loading FXML: " + e.getMessage(), e);
                    }

                    Thread.sleep(10); // Optional delay for effect
                }

                return null;
            }

            @Override
            protected void failed() {
                System.err.println("Error during data table loading: " + getException().getMessage());
            }
        };

        new Thread(preloadTask).start();
    }

    public void preLoadStudentsData(List<Student> students, String path, PreloadType preloadType) {
        if (!vBoxUserList.getChildren().isEmpty() && preloadType == PreloadType.RESET) {
            vBoxUserList.getChildren().clear();
        }

        Task<Void> preloadTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                for (Student student : students) {
                    try {
                        String[] studentData = new String[] {
                                student.getUserRole(),
                                student.getName(),
                                student.getMajor(),
                                student.getEmail(),
                                student.getStudentId()
                        };

                        Pane scene = loadScene(path, studentData);
                        scene.setId(student.getStudentId());

                        Platform.runLater(() -> vBoxUserList.getChildren().add(scene));
                    } catch (IOException e) {
                        throw new RuntimeException("Error loading FXML: " + e.getMessage(), e);
                    }

                    Thread.sleep(10); // Optional delay for effect
                }

                return null;
            }

            @Override
            protected void failed() {
                System.err.println("Error during data table loading: " + getException().getMessage());
            }
        };

        new Thread(preloadTask).start();
    }

    private Pane loadScene(String path, String[] data) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/admin/" + path));
        Pane scene = fxmlLoader.load();
        String[] formattedData = new String[] { data[4], data[1], data[2], data[3] };

        switch (path) {
            case "admin-users-student-bar.fxml" -> {
                AdminUsersStudentBarController controller = fxmlLoader.getController();
                controller.setData(formattedData);
            }

            case "admin-users-guest-bar.fxml" -> {
                AdminUsersGuestBarController controller = fxmlLoader.getController();
                controller.setData(formattedData);
            }

            default -> throw new IllegalArgumentException("Unknown FXML path: " + path);
        }

        return scene;
    }

    private void removeUserFromVBox(String userId) {
        for (int i = 0; i < vBoxUserList.getChildren().size(); i++) {
            if (vBoxUserList.getChildren().get(i).getId() != null
                    && vBoxUserList.getChildren().get(i).getId().equals(userId)) {
                int finalI = i;
                Platform.runLater(() -> vBoxUserList.getChildren().remove(finalI));
                return;
            }
        }
    }

    @FXML
    void studentButtonOnAction(ActionEvent event) {
        if (status == EnumUtils.UserType.STUDENT) {
            return;
        }

        status = EnumUtils.UserType.STUDENT;
        setDefaultStyle();
        studentPane.setStyle("-fx-background-color: #000; -fx-background-radius: 12px;");
        studentLabel.setStyle("-fx-text-fill: white;");
        setVisibility(true, false);
        showStudentsList();
    }

    public void showStudentsList() {
        vBoxUserList.getChildren().clear();
        preLoadStudentsData(studentsData, "admin-users-student-bar.fxml", PreloadType.RESET);
    }

    @FXML
    void guestButtonOnAction(ActionEvent event) {
        if (status == EnumUtils.UserType.GUEST) {
            return;
        }

        status = EnumUtils.UserType.GUEST;
        setDefaultStyle();
        guestPane.setStyle("-fx-background-color: #000; -fx-background-radius: 12px;");
        guestLabel.setStyle("-fx-text-fill: white;");
        setVisibility(false, true);
        showGuestsList();
    }

    private void showGuestsList() {
        vBoxUserList.getChildren().clear();
        preloadExternalBorrowerData(guestsData, "admin-users-guest-bar.fxml", PreloadType.RESET);
    }

    @FXML
    void addUserButtonOnAction(ActionEvent event) {
        Platform.runLater(() -> {
            ChangeScene.openAdminPopUp(stackPaneContainer, "/fxml/admin/admin-add-user-dialog.fxml",
                    EnumUtils.PopupList.ADD_USER);
        });
    }

    @FXML
    void btnRefreshTableOnAction(ActionEvent event) {
        refreshUsersList();
    }

    public void refreshUsersList() {
        vBoxUserList.getChildren().clear();
        if (status == EnumUtils.UserType.STUDENT) {
            adminGlobalController.getObservableStudentsData().clear();
        } else {
            adminGlobalController.getObservableExternalBorrowersData().clear();
        }
        Task<List<User>> reloadTask = new Task<>() {
            @Override
            protected List<User> call() {
                if (status == EnumUtils.UserType.STUDENT) {
                    return adminGlobalController.preLoadStudentsData().stream()
                            .map(student -> (User) student)
                            .toList();
                } else {
                    return adminGlobalController.preLoadExternalBorrowersData().stream()
                            .map(externalBorrower -> (User) externalBorrower)
                            .toList();
                }
            }

            @Override
            protected void succeeded() {
                if (status == EnumUtils.UserType.STUDENT) {
                    adminGlobalController.getObservableStudentsData().addAll(getValue().stream()
                            .map(user -> (Student) user)
                            .toList());
                    preLoadStudentsData(studentsData, "admin-users-student-bar.fxml", PreloadType.RESET);
                    textSearch.clear();
                    textSearch.setEditable(true);
                } else {
                    adminGlobalController.getObservableExternalBorrowersData().addAll(
                        getValue().stream().map(user -> (ExternalBorrower) user).toList()
                    );
                    preloadExternalBorrowerData(guestsData, "admin-users-guest-bar.fxml", PreloadType.RESET);
                    textSearch.clear();
                    textSearch.setEditable(true);
                }
            }

            @Override
            protected void failed() {
                System.out.println("Failed to reload" + getException().getMessage());
            }
        };

        new Thread(reloadTask).start();
    }

    public void performSearch() {
        vBoxUserList.getChildren().clear();
        String searchText = textSearch.getText();
        if (searchText.isEmpty()) {
            if (status == EnumUtils.UserType.STUDENT) {
                showStudentsList();
            } else if (status == EnumUtils.UserType.GUEST) {
                showGuestsList();
            }
        } else {
            showFilteredData(searchText);
        }
        textSearch.setEditable(true);
    }

    // Filtering Logic
    public void showFilteredData(String searchText) {
        if (status == EnumUtils.UserType.STUDENT) {
            adminGlobalController.getObservableStudentsData().stream()
                    .filter(student -> (student.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                            student.getStudentId().toLowerCase().contains(searchText.toLowerCase())) ||
                            student.getMajor().toLowerCase().contains(searchText.toLowerCase()) ||
                            student.getEmail().toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(student -> {
                        try {
                            String[] studentData = new String[] {
                                    student.getUserRole(),
                                    student.getName(),
                                    student.getMajor(),
                                    student.getEmail(),
                                    student.getStudentId()
                            };
                            Pane scene = loadScene("admin-users-student-bar.fxml", studentData);
                            scene.setId(student.getStudentId());

                            Platform.runLater(() -> vBoxUserList.getChildren().add(scene));
                        } catch (IOException e) {
                            throw new RuntimeException("Error loading FXML: " + e.getMessage(), e);
                        }

                    });
        } else {
            adminGlobalController.getObservableExternalBorrowersData().stream()
                    .filter(externalBorrower -> (externalBorrower.getName().toLowerCase()
                            .contains(searchText.toLowerCase())) ||
                            externalBorrower.getSocialId().toLowerCase().contains(searchText.toLowerCase()) ||
                            externalBorrower.getEmail().toLowerCase().contains(searchText.toLowerCase()) ||
                            externalBorrower.getPhoneNumber().toLowerCase().contains(searchText.toLowerCase()))
                    .forEach(externalBorrower -> {
                        try {
                            String[] externalBorrowerData = new String[] {
                                    externalBorrower.getUserRole(),
                                    externalBorrower.getName(),
                                    externalBorrower.getPhoneNumber(),
                                    externalBorrower.getEmail(),
                                    externalBorrower.getSocialId()
                            };
                            Pane scene = loadScene("admin-users-guest-bar.fxml", externalBorrowerData);
                            scene.setId(externalBorrower.getSocialId());

                            Platform.runLater(() -> vBoxUserList.getChildren().add(scene));
                        } catch (IOException e) {
                            throw new RuntimeException("Error loading FXML: " + e.getMessage(), e);
                        }
                    });
        }
    }

    @FXML
    void btnOnMouseEntered(MouseEvent event) {
        Object source = event.getSource();
        if (source == studentButton) {
            AnimationUtils.createScaleTransition(AnimationUtils.HOVER_SCALE, studentPane).play();
        } else if (source == guestButton) {
            AnimationUtils.createScaleTransition(AnimationUtils.HOVER_SCALE, guestPane).play();
        } else if (source == refreshButton) {
            AnimationUtils.createScaleTransition(1.15, refreshPaneButton).play();
        } else if (source == searchPane) {
            AnimationUtils.createScaleTransition(1.05, searchPane).play();
        }
    }

    @FXML
    void btnOnMouseExited(MouseEvent event) {
        Object source = event.getSource();
        if (source == studentButton) {
            AnimationUtils.createScaleTransition(1, studentPane).play();
        } else if (source == guestButton) {
            AnimationUtils.createScaleTransition(1, guestPane).play();
        } else if (source == refreshButton) {
            AnimationUtils.createScaleTransition(1, refreshPaneButton).play();
        } else if (source == searchPane) {
            AnimationUtils.createScaleTransition(1, searchPane).play();
        }
    }

    @FXML
    void btnAddUserOnMouseEntered(MouseEvent event) {
        hBoxAddUser.setStyle(
                "-fx-background-color: #F2F2F2; -fx-background-radius: 12px; -fx-border-color: #000; -fx-border-radius: 12px; -fx-border-width: 1.2px;");
        addImage.setImage(new Image(getClass().getResource(hoverAcquireLogo).toExternalForm()));
        addLabel.setStyle("-fx-text-fill: #000;");
        AnimationUtils.createScaleTransition(AnimationUtils.HOVER_SCALE, hBoxAddUser).play();
    }

    @FXML
    void btnAddUserOnMouseExited(MouseEvent event) {
        hBoxAddUser.setStyle("-fx-background-color: #000; -fx-background-radius: 12px;");
        addImage.setImage(new Image(getClass().getResource(acquireLogo).toExternalForm()));
        addLabel.setStyle("-fx-text-fill: #F2F2F2;");
        AnimationUtils.createScaleTransition(AnimationUtils.DEFAULT_SCALE, hBoxAddUser).play();
    }

    public void setDefaultStyle() {
        studentPane.setStyle("-fx-background-color: #e3e3e3; -fx-background-radius: 12px;");
        studentLabel.setStyle("-fx-text-fill: #000");
        guestPane.setStyle("-fx-background-color: #e3e3e3; -fx-background-radius: 12px;");
        guestLabel.setStyle("-fx-text-fill: #000");
    }

    public EnumUtils.UserType getStatus() {
        return status;
    }

    public List<Student> getStudentsData() {
        return studentsData;
    }

    public List<ExternalBorrower> getGuestsData() {
        return guestsData;
    }

    public enum PreloadType {
        RESET, ADD
    }

}
