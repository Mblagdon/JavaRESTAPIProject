// Ensuring save
package com.example.javarestapiproject;

public class Association {
    private int authorId;
    private String isbn;

    public Association() {}

    public Association(int authorId, String isbn) {
        this.authorId = authorId;
        this.isbn = isbn;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }
}

