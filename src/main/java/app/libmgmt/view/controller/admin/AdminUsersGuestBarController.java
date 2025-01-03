package app.libmgmt.view.controller.admin;

import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import app.libmgmt.model.ExternalBorrower;
import app.libmgmt.model.User;
import app.libmgmt.service.UserService;
import app.libmgmt.util.ChangeScene;
import app.libmgmt.util.EnumUtils;

public class AdminUsersGuestBarController {

    @FXML
    private Label emailLabel, idLabel, phoneLabel, nameLabel;
    @FXML
    private HBox hBoxUser;
    @FXML
    private ImageView deleteFunction, editFunction, viewFunction, bookViewFunction;

    @FXML
    public void initialize() {
        // Setup listener to handle updates in user data for GUEST type
        AdminGlobalController.getInstance().getObservableExternalBorrowersData()
                .addListener((ListChangeListener<User>) change -> {
                    while (change.next()) {
                        if (change.wasReplaced() && change.getFrom() >= 0
                                && change.getFrom() < change.getList().size()) {
                            ExternalBorrower updatedUser = (ExternalBorrower) change.getList().get(change.getFrom());
                            // data format: [name, phone, email, id, password]
                            String[] updatedUserData = new String[] {
                                    updatedUser.getName(),
                                    updatedUser.getPhoneNumber(),
                                    updatedUser.getEmail(),
                                    updatedUser.getSocialId(),
                                    updatedUser.getPassword()
                            };
                            User updatedUserObj = new ExternalBorrower(updatedUserData);
                            UserService userService = new UserService();
                            userService.updateUser(updatedUserObj);

                            if (idLabel.getText().equals(updatedUserData[3])) {
                                setUpdateData(updatedUserData);
                            }
                        }
                    }
                });
    }

    // Sets initial user data
    public void setData(String[] data) {
        idLabel.setText(data[0]);
        nameLabel.setText(data[1]);
        phoneLabel.setText(data[2]);
        emailLabel.setText(data[3]);
    }

    // Updates user data when changes occur
    public void setUpdateData(String[] data) {
        // data format: [name, major, email, id, password]
        idLabel.setText(data[3]);
        nameLabel.setText(data[0]);
        phoneLabel.setText(data[1]);
        emailLabel.setText(data[2]);
    }

    // Gets user data as an array
    public String[] getData() {
        return new String[] { nameLabel.getText(), phoneLabel.getText(),
                emailLabel.getText(), idLabel.getText() };
    }

    @FXML
    void imgBookOnMouseClicked(MouseEvent event) {
        System.out.println("View Borrowed Books");
        ChangeScene.openAdminPopUp(AdminUsersLayoutController.getInstance().stackPaneContainer,
                "/fxml/admin/admin-all-borrowed-books-view-dialog.fxml", EnumUtils.PopupList.ALL_BORROWED_BOOKS_VIEW);
        AdminAllBorrowedBookViewDialogController.getInstance().setData(idLabel.getText());
        AdminAllBorrowedBookViewDialogController.getInstance().preLoadData();
    }

    @FXML
    void imgViewOnMouseClicked(MouseEvent event) {
        System.out.println("View");
        ChangeScene.openAdminPopUp(AdminUsersLayoutController.getInstance().stackPaneContainer,
                "/fxml/admin/admin-users-view-dialog.fxml", EnumUtils.PopupList.USER_VIEW);
        AdminUsersViewDialogController.getInstance().setData(getData(), EnumUtils.UserType.GUEST);
    }

    @FXML
    void imgEditOnMouseClicked(MouseEvent event) {
        System.out.println("Edit");
        ChangeScene.openAdminPopUp(AdminUsersLayoutController.getInstance().stackPaneContainer,
                "/fxml/admin/admin-users-edit-dialog.fxml", EnumUtils.PopupList.USER_EDIT);
        AdminUsersEditDialogController.getInstance().showOriginalUserData(getData(), EnumUtils.UserType.GUEST);
    }

    @FXML
    void imgDeleteOnMouseClicked(MouseEvent event) {
        System.out.println("Delete");
        ChangeScene.openAdminPopUp(
                AdminUsersLayoutController.getInstance().stackPaneContainer,
                "/fxml/admin/admin-delete-confirmation-dialog.fxml",
                idLabel.getText(),
                EnumUtils.PopupList.GUEST_DELETE);
    }

    @FXML
    void imgViewOnMouseEntered(MouseEvent event) {
        Image hoverImage = new Image(getClass().getResource("/assets/icon/Property 1=Variant2" +
                ".png").toExternalForm());
        viewFunction.setImage(hoverImage);
    }

    @FXML
    void imgViewOnMouseExited(MouseEvent event) {
        Image normalImage = new Image(getClass().getResource("/assets/icon/btn view.png").toExternalForm());
        viewFunction.setImage(normalImage);
    }

    @FXML
    void imgEditOnMouseEntered(MouseEvent event) {
        Image hoverImage = new Image(getClass().getResource("/assets/icon/edit2.png").toExternalForm());
        editFunction.setImage(hoverImage);
    }

    @FXML
    void imgEditOnMouseExited(MouseEvent event) {
        Image normalImage = new Image(getClass().getResource("/assets/icon/btn edit.png").toExternalForm());
        editFunction.setImage(normalImage);
    }

    @FXML
    void imgDeleteOnMouseEntered(MouseEvent event) {
        Image hoverImage = new Image(getClass().getResource("/assets/icon/red-recycle.png").toExternalForm());
        deleteFunction.setImage(hoverImage);
    }

    @FXML
    void imgDeleteOnMouseExited(MouseEvent event) {
        Image normalImage = new Image(getClass().getResource("/assets/icon/btn Delete.png").toExternalForm());
        deleteFunction.setImage(normalImage);
    }

}
