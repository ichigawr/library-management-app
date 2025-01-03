package app.libmgmt.view.controller.admin;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXRadioButton;

import app.libmgmt.model.Admin;
import app.libmgmt.model.Student;
import app.libmgmt.model.ExternalBorrower;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import app.libmgmt.service.UserService;
import app.libmgmt.util.AnimationUtils;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.EnumUtils;
import app.libmgmt.util.RegExPatterns;

import java.util.ArrayList;
import java.util.List;

public class AdminAddUserDialogController {

    private final String[] majorList = EnumUtils.UETMajor;
    AdminUsersLayoutController adminUsersLayoutController = AdminUsersLayoutController.getInstance();
    AdminGlobalController adminGlobalController = AdminGlobalController.getInstance();

    private UserService userService = new UserService();

    // FXML UI components
    @FXML
    private ToggleGroup addUserType;
    @FXML
    private Pane adminPane, container, guestPane, studentPane;
    @FXML
    private JFXRadioButton adminRadioBtn, guestRadioBtn, studentRadioBtn;
    @FXML
    private Label notificationLabel;
    @FXML
    private JFXComboBox<String> cbbMajorStudent;
    @FXML
    private PasswordField txtCfPasswordAdmin, txtCfPasswordGuest, txtCfPasswordStudent;
    @FXML
    private TextField txtEmailAdmin, txtEmailGuest, txtEmailStudent, txtIDStudent, txtIdGuest,
            txtNameAdmin, txtNameGuest, txtNameStudent, txtPhoneNumberGuest, txtIdAdmin;
    @FXML
    private PasswordField txtPasswordAdmin, txtPasswordGuest, txtPasswordStudent;
    @FXML
    private JFXButton closeDialogButton;
    @FXML
    private ImageView imgClose;

    public void initialize() {
        cbbMajorStudent.getItems().addAll(majorList);

        container.setOnMouseClicked(event -> container.requestFocus());

        AnimationUtils.hoverCloseIcons(closeDialogButton, imgClose);

        setDefaultPane();
        studentPane.setVisible(true);
    }

    // --- Helper Methods ---
    private void setDefaultPane() {
        studentPane.setVisible(false);
        guestPane.setVisible(false);
        adminPane.setVisible(false);
    }

    private void setDefaultContent() {
        txtIdAdmin.setText("");
        txtNameAdmin.setText("");
        txtEmailAdmin.setText("");
        txtPasswordAdmin.setText("");
        txtCfPasswordAdmin.setText("");
        txtNameStudent.setText("");
        txtEmailStudent.setText("");
        txtPasswordStudent.setText("");
        txtCfPasswordStudent.setText("");
        txtIDStudent.setText("");
        cbbMajorStudent.getSelectionModel().clearSelection();
        txtNameGuest.setText("");
        txtEmailGuest.setText("");
        txtPasswordGuest.setText("");
        txtCfPasswordGuest.setText("");
        txtPhoneNumberGuest.setText("");
        txtIdGuest.setText("");
    }

    private void showNotification(String message, String color) {
        notificationLabel.setText(message);
        AnimationUtils.playNotificationTimeline(notificationLabel, 3, color);
    }

    private boolean isFieldEmpty(String... fields) {
        for (String field : fields) {
            if (field.isEmpty())
                return true;
        }
        return false;
    }

    // --- Validation Methods ---
    private boolean checkValidAdmin(String[] adminInfo, EnumUtils.UserType userType) {
        String id = adminInfo[3];
        String name = adminInfo[0];
        String email = adminInfo[2];
        String password = adminInfo[4];
        String cfPassword = adminInfo[5];

        if (isFieldEmpty(id, name, email, password, cfPassword)) {
            showNotification("Please fill in all fields.", "red");
            return false;
        }

        if (!password.equals(cfPassword)) {
            showNotification("Password does not match.", "red");
            return false;
        }

        if (!RegExPatterns.emailPattern(email)) {
            showNotification("Invalid email.", "red");
            return false;
        }

        if (!RegExPatterns.passwordPattern(password)) {
            showNotification("Password must contain at least 8 characters.", "red");
            return false;
        }

        return true;
    }

    private boolean checkValidUser(String[] userInfo, EnumUtils.UserType userType) {
        String name = userInfo[0];
        String majorOrContact = userInfo[1];
        String email = userInfo[2];
        String id = userInfo[3];
        String password = userInfo[4];
        String cfPassword = userInfo[5];

        if (isFieldEmpty(name, email, password, cfPassword, id, majorOrContact)) {
            showNotification("Please fill in all fields.", "red");
            return false;
        }

        if (!password.equals(cfPassword)) {
            showNotification("Password does not match.", "red");
            return false;
        }

        if (userType == EnumUtils.UserType.STUDENT) {
            if (!RegExPatterns.studentIDPattern(id)) {
                showNotification("Invalid student ID.", "red");
                return false;
            }

            if (majorOrContact == null) {
                showNotification("Please select a major.", "red");
                return false;
            }
        }

        if (userType == EnumUtils.UserType.GUEST) {
            if (!RegExPatterns.citizenIDPattern(id)) {
                showNotification("Invalid guest ID.", "red");
                return false;
            }

            if (!RegExPatterns.phoneNumberPattern(majorOrContact)) {
                showNotification("Invalid phone number.", "red");
                return false;
            }
        }

        if (!RegExPatterns.emailPattern(email)) {
            showNotification("Invalid email.", "red");
            return false;
        }

        if (!RegExPatterns.passwordPattern(password)) {
            showNotification("Password must contain at least 8 characters.", "red");
            return false;
        }

        return true;
    }

    // --- User Add Methods ---
    private void addAdmin(String[] adminInfo) {
        if (checkValidAdmin(adminInfo, EnumUtils.UserType.ADMIN)) {
            String[] newUser = { adminInfo[0], "major", adminInfo[2], adminInfo[3], adminInfo[4] };
            Admin admin = new Admin(newUser);
            AdminGlobalController.getInstance().getAdminData().add(admin);
            userService.addUser(admin);
            AdminDashboardController.getInstance().loadAdminDataTable(adminInfo[1], adminInfo[2], adminInfo[0]);
            showNotification("Added successfully", "#08a80d");
            setDefaultContent();
        }
    }

    private void addStudent(String[] studentInfo) {
        //student data format: [name, major, email, id, password, cfPassword]
        if (checkValidUser(studentInfo, EnumUtils.UserType.STUDENT)) {
            List<Student> userData = new ArrayList<>();
            String[] newUser = { studentInfo[0], studentInfo[1], studentInfo[2], studentInfo[3], studentInfo[4] };
            //data format: [name, major, email, id, password]
            Student newStudent = new Student(newUser);
            System.out.println("Adding student");
            userData.add(newStudent);
            userService.addUser(newStudent);

            if (adminUsersLayoutController.getStatus() == EnumUtils.UserType.STUDENT) {
                adminUsersLayoutController.preLoadStudentsData(userData, "admin-users-student-bar.fxml",
                        AdminUsersLayoutController.PreloadType.ADD);
            }

            adminGlobalController.getObservableStudentsData().add(newStudent);
            showNotification("Added successfully", "#08a80d");
            setDefaultContent();
        }
    }

    private void addGuest(String[] guestInfo) {
        //guest data format: [name, phone, email, id, password, cfPassword]
        if (checkValidUser(guestInfo, EnumUtils.UserType.GUEST)) {
            List<ExternalBorrower> userData = new ArrayList<>();

            String[] newUser = {guestInfo[0], guestInfo[1], guestInfo[2], guestInfo[3],
                    guestInfo[4] };
            //data format: [name, phone, email, id, password]
            ExternalBorrower newGuest = new ExternalBorrower(newUser);
            System.out.println("Adding guest");
            userData.add(newGuest);
            userService.addUser(newGuest);

            if (adminUsersLayoutController.getStatus() == EnumUtils.UserType.GUEST) {
                adminUsersLayoutController.preloadExternalBorrowerData(userData, "admin-users-guest-bar.fxml",
                        AdminUsersLayoutController.PreloadType.ADD);
            }
            adminGlobalController.getObservableExternalBorrowersData().add(newGuest);
            showNotification("Added successfully", "#08a80d");
        }
    }

    // --- Event Handlers ---
    @FXML
    void studentRadioBtnOnAction(ActionEvent event) {
        setDefaultPane();
        setDefaultContent();
        studentPane.setVisible(true);
        AnimationUtils.zoomIn(studentPane, 1);
    }

    @FXML
    void guestRadioBtnOnAction(ActionEvent event) {
        setDefaultPane();
        setDefaultContent();
        guestPane.setVisible(true);
        AnimationUtils.zoomIn(guestPane, 1);
    }

    @FXML
    void adminRadioBtnOnAction(ActionEvent event) {
        setDefaultPane();
        setDefaultContent();
        adminPane.setVisible(true);
        AnimationUtils.zoomIn(adminPane, 1);
    }

    @FXML
    void addButtonOnAction(ActionEvent event) {
        RadioButton selectedRadioButton = (RadioButton) addUserType.getSelectedToggle();

        if (selectedRadioButton == studentRadioBtn) {
            System.out.println("Add Student");
            //format: [name, major, email, id, password, cfPassword]
            String[] studentInfo = { txtNameStudent.getText(), cbbMajorStudent.getSelectionModel().getSelectedItem(),
                    txtEmailStudent.getText(), txtIDStudent.getText(), txtPasswordStudent.getText(),
                    txtCfPasswordStudent.getText() };
            addStudent(studentInfo);

        } else if (selectedRadioButton == guestRadioBtn) {
            System.out.println("Add Guest");
            //format: [name, phone, email, id, password, cfPassword]
            String[] guestInfo = { txtNameGuest.getText(), txtPhoneNumberGuest.getText(),
                    txtEmailGuest.getText(), txtIdGuest.getText(), txtPasswordGuest.getText(),
                    txtCfPasswordGuest.getText() };
            addGuest(guestInfo);

        } else if (selectedRadioButton == adminRadioBtn) {
            System.out.println("Add Admin");
            //data format: [name, major, email, id, password]
            String[] adminInfo = {txtNameAdmin.getText(), "major", txtEmailAdmin.getText(),
                    txtIdAdmin.getText(), txtPasswordAdmin.getText(), txtCfPasswordAdmin.getText()};
            addAdmin(adminInfo);
        }
    }

    @FXML
    void cancelButtonOnAction(ActionEvent event) {
        setDefaultContent();
    }

    @FXML
    void closeButtonOnAction(ActionEvent event) {
        ChangeScene.closePopUp();
    }
}
