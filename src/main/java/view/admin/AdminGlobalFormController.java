package view.admin;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import util.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

public class AdminGlobalFormController {


    private static AdminGlobalFormController controller;
    private final List<String[]> borrowedBooksData = preLoadBorrowedBooksData();
    private final List<String[]> booksData = preLoadBooksData();
    private final List<String[]> usersData = preLoadUsersData();
    private final List<String[]> adminData = preLoadAdminData();
    @FXML
    private Pane pagingPane;
    @FXML
    private StackPane stackPaneContainer;

    public AdminGlobalFormController() {
        controller = this;
    }

    public static AdminGlobalFormController getInstance() {
        return controller;
    }

    public void initialize() {
        System.out.println("Admin Global Form initialized");

        AnimationUtils.fadeInRight(pagingPane);
    }

    public Pane getPagingPane() {
        return pagingPane;
    }

    public void setPagingPane(Pane pagingPane) {
        this.pagingPane = pagingPane;
    }

    public List<String[]> preLoadBorrowedBooksData() {
        List<String[]> data = new ArrayList<>();

        data.add(new String[]{"Hoang Duy Thinh", "23020708", "1", "13-04-2024", "14-08-2024"});
        data.add(new String[]{"Luis Suarez", "23020710", "1", "13-11-2024", "14-08-2024"});
        data.add(new String[]{"Picasso", "23020714", "1", "13-08-2022", "14-08-2024"});
        data.add(new String[]{"Van Gogh", "23020715", "1", "13-08-2024", "14-08-2024"});
        data.add(new String[]{"Cristiano Ronaldo", "23020712", "1", "13-08-2024", "14-08-2024"});
        data.add(new String[]{"Lionel Messi", "23020713", "1", "13-08-2024", "14-08-2024"});

        return data;
    }

    public List<String[]> preLoadBooksData() {
        List<String[]> data = new ArrayList<>();

        data.add(new String[]{"7", "https://www.thejapanshop.com/cdn/shop/products/new_doc_91_1_1280x.jpg?v=1571438916", "Doraemon",
                "Comic", "Fujko F Fujio", "0", "NXB Trẻ", "13-08-2024"});
        data.add(new String[]{"1", "https://marketplace.canva.com/EAFaQMYuZbo/1/0/1003w/canva-brown-rusty-mystery-novel-book-cover-hG1QhA7BiBU.jpg"
                , "The Great Gatsby", "Education", "F. Scott Fitzgerald", "3", "NXB Trẻ", "13-08" +
                "-2024"});
        data.add(new String[]{"2", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS66i6hTBkniGtDdwxyi4hA3PFm2mJ0GUIDxw&s", "To " +
                "Kill a Mockingbird", "Education", "Harper Lee", "4", "NXB Trẻ", "13-08-2024"});
        data.add(new String[]{"3", "https://thuviensach.vn/img/news/2022/09/larger/1011-1984-1.jpg?v=8882", "1984", "Education", "George Orwell",
                "0", "NXB Trẻ", "13-08-2024"});
        data.add(new String[]{"4", "https://play-lh.googleusercontent.com/f1jkKDk5wKz1CZMyNjOR7klTu-ORIZs9sBMWSOVtd09GE6ulfiW5M4FmWrS54CZmCDiZ", "Pride & Prejudice",
                "Education", "J.D. Salinger", "12", "NXB Trẻ", "13-08-2024"});
        data.add(new String[]{"5", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRYqVdifswPLs8J53knQLpfO0dYIVMq4Mu14w&s", "Sherlock Holmes",
                "Detective", "Arthur Conan Doyle", "32", "NXB Trẻ", "13-08-2024"});
        data.add(new String[]{"6", "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQapwj529X6xqxmWUlrZAbQLhi-jEpU1-gx8A&s", "Dracula", "Horror",
                "Bram Stoker", "0", "NXB Trẻ", "13-08-2024"});
        data.add(new String[]{"7", "https://www.thejapanshop.com/cdn/shop/products/new_doc_91_1_1280x.jpg?v=1571438916", "Doraemon",
                "Comic", "Fujko F Fujio", "0", "NXB Trẻ", "13-08-2024"});

        return data;
    }

    public List<String[]> preLoadUsersData() {
        List<String[]> data = new ArrayList<>();

        data.add(new String[]{"Student", "Hoang Duy Thinh", "CN1 - Information Technology",
                "23020708@vnu.edu.vn", "23020708"});
        data.add(new String[]{"External Borrower", "Pham Manh Hung", "0916621455", "hungmanh" +
                "@gmail.com", "037205005003"});

        return data;
    }

    public List<String[]> preLoadAdminData() {
        List<String[]> data = new ArrayList<>();

        data.add(new String[]{"H D Thịnh", "23020708@vnu.edu.vn"});
        data.add(new String[]{"N X Vinh", "23020101@vnu.edu.vn"});
        data.add(new String[]{"L M Tường", "23020102@vnu.edu.vn"});
        data.add(new String[]{"N Đ Nguyên", "23020103@vnu.edu.vn"});

        return data;
    }

    public List<String[]> getBorrowedBooksData() {
        return borrowedBooksData;
    }

    public List<String[]> getBooksData() {
        return booksData;
    }

    public List<String[]> getUsersData() {
        return usersData;
    }

    public List<String[]> getAdminData() {
        return adminData;
    }

    public StackPane getStackPaneContainer() {
        return stackPaneContainer;
    }

    public String[] getBookDataById(String id) {
        for (String[] data : booksData) {
            if (data[0].equals(id)) {
                return data;
            }
        }
        return null;
    }

    public String[] getUserDataById(String id) {
        for (String[] data : usersData) {
            if (data[4].equals(id)) {
                return data;
            }
        }
        return null;
    }

    public void deleteBookDataById(String id) {
        for (String[] data : booksData) {
            if (data[0].equals(id)) {
                booksData.remove(data);
                break;
            }
        }
    }
}
