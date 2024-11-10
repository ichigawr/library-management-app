package app.libmgmt.model;

import java.util.Date;
import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private Date publishedDate;
    private String publisher;
    private String coverUrl;
    private int availableCopies;
    private List<String> authors;
    private List<String> categories;

    public Book(String isbn, String title, Date publishedDate, String publisher, String coverUrl,
            int availableCopies, List<String> authors, List<String> categories) {
        this.isbn = isbn;
        this.title = title;
        this.publishedDate = publishedDate;
        this.publisher = publisher;
        this.coverUrl = coverUrl;
        this.availableCopies = availableCopies;
        this.authors = authors;
        this.categories = categories;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(Date publishedDate) {
        this.publishedDate = publishedDate;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCoverUrl() {
        return coverUrl;
    }

    public void setCoverUrl(String coverUrl) {
        this.coverUrl = coverUrl;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public void updateAvailableCopies(int availableCopies) {
        this.availableCopies = availableCopies;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public List<String> getCategories() {
        return categories;
    }
}