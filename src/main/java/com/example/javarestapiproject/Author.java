// Ensuring save
package com.example.javarestapiproject;

import java.util.ArrayList;
import java.util.List;

public class Author {
    private int id;
    private String firstName;
    private String lastName;
    private List<Book.BookDTO> books;

    public Author() {
        this.books = new ArrayList<>();
    }

    public Author(int id, String firstName, String lastName) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.books = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public List<Book.BookDTO> getBooks() {
        return books;
    }

    public void setBooks(List<Book.BookDTO> books) {
        this.books = books;
    }

    public void addBookDTO(Book.BookDTO bookDTO) {
        if (bookDTO != null && !this.books.contains(bookDTO)) {
            this.books.add(bookDTO);
        }
    }

    public static class AuthorDTO {
        private int id;
        private String firstName;
        private String lastName;

        public AuthorDTO(int id, String firstName, String lastName) {
            this.id = id;
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public int getId() {
            return id;
        }

        public String getFirstName() {
            return firstName;
        }

        public String getLastName() {
            return lastName;
        }
    }

    @Override
    public String toString() {
        return "Author [ID: " + id + ", Name: " + firstName + " " + lastName + "]";
    }
}

