// Ensuring save
package com.example.javarestapiproject;

import java.util.ArrayList;
import java.util.List;

public class Book {
    private String isbn;
    private String title;
    private int editionNumber;
    private String copyright;
    private List<Author.AuthorDTO> authorList;

    public Book(String isbn, String title, int editionNumber, String copyright) {
        this.isbn = isbn;
        this.title = title;
        this.editionNumber = editionNumber;
        this.copyright = copyright;
        this.authorList = new ArrayList<>();
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEditionNumber() {
        return editionNumber;
    }

    public void setEditionNumber(int editionNumber) {
        this.editionNumber = editionNumber;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public List<Author.AuthorDTO> getAuthorList() {
        return authorList;
    }

    public void setAuthorList(List<Author.AuthorDTO> authorList) {
        this.authorList = authorList;
    }

    public void addAuthorDTO(Author.AuthorDTO authorDTO) {
        if (authorDTO != null && !this.authorList.contains(authorDTO)) {
            this.authorList.add(authorDTO);
        }
    }

    public Book() {
        this.authorList = new ArrayList<>();
    }
    public static class BookDTO {
        private String isbn;
        private String title;
        private int editionNumber;
        private String copyright;

        public BookDTO(String isbn, String title, int editionNumber, String copyright) {
            this.isbn = isbn;
            this.title = title;
            this.editionNumber = editionNumber;
            this.copyright = copyright;
        }

        public String getIsbn() {
            return isbn;
        }

        public void setIsbn(String isbn) {
            this.isbn = isbn;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public int getEditionNumber() {
            return editionNumber;
        }

        public void setEditionNumber(int editionNumber) {
            this.editionNumber = editionNumber;
        }

        public String getCopyright() {
            return copyright;
        }

        public void setCopyright(String copyright) {
            this.copyright = copyright;
        }
    }

    @Override
    public String toString() {
        StringBuilder authors = new StringBuilder();
        for (Author.AuthorDTO authorDTO : authorList) {
            authors.append(authorDTO.getFirstName()).append(" ").append(authorDTO.getLastName()).append(", ");
        }
        // Remove the trailing comma and space
        if (authors.length() > 0) {
            authors.setLength(authors.length() - 2);
        }
        return "Book [ISBN: " + isbn + ", Title: " + title + ", Authors: " + authors.toString() + "]";
    }
}

