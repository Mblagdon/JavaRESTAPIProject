// Ensuring save
package com.example.javarestapiproject;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Path("/library")
public class LibraryController {
    // Default URI/URL with welcome message/general info for api
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String defaultInfo() {
        return "Welcome to Marcus' Book Management API!\n" +
                "Here you can browse books, authors, add books, add authors,\n" +
                "modify books, modify authors, delete books, delete authors, and more!";
    }
    // Get all books
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/books
    @GET
    @Path("/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBooks() {
        List<Book> books = new ArrayList<>();

        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "SELECT * FROM titles";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(rs.getString("isbn"), rs.getString("title"), rs.getInt("editionNumber"), rs.getString("copyright"));
                String SQLAuthors = "SELECT a.* FROM authors a INNER JOIN authorisbn ai ON a.authorID = ai.authorID WHERE ai.isbn = ?";
                PreparedStatement stmtAuthors = connection.prepareStatement(SQLAuthors);
                stmtAuthors.setString(1, book.getIsbn());
                ResultSet rsAuthors = stmtAuthors.executeQuery();

                while (rsAuthors.next()) {
                    Author author = new Author(rsAuthors.getInt("authorID"), rsAuthors.getString("firstName"), rsAuthors.getString("lastName"));
                    Author.AuthorDTO authorDTO = new Author.AuthorDTO(author.getId(), author.getFirstName(), author.getLastName());
                    book.getAuthorList().add(authorDTO);
                }
                books.add(book);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok(books).build();
    }
    // Get all authors
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/authors
    @GET
    @Path("/authors")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllAuthors() {
        List<Author> authors = new ArrayList<>();

        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "SELECT * FROM authors";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Author author = new Author(rs.getInt("authorID"), rs.getString("firstName"), rs.getString("lastName"));

                String SQLBooks = "SELECT t.* FROM titles t INNER JOIN authorisbn ai ON t.isbn = ai.isbn WHERE ai.authorID = ?";
                try (PreparedStatement stmtBooks = connection.prepareStatement(SQLBooks)) {
                    stmtBooks.setInt(1, author.getId());
                    ResultSet rsBooks = stmtBooks.executeQuery();

                    while (rsBooks.next()) {
                        Book.BookDTO bookDTO = new Book.BookDTO(
                                rsBooks.getString("isbn"),
                                rsBooks.getString("title"),
                                rsBooks.getInt("editionNumber"),
                                rsBooks.getString("copyright")
                        );
                        author.getBooks().add(bookDTO);
                    }
                }
                authors.add(author);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok(authors).build();
    }
    // URI for getting specific book
    // id is isbn
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/book/0133807800
    @GET
    @Path("/book/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("id") String isbn) {
        Book book = null;

        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "SELECT * FROM titles WHERE isbn = ?";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, isbn);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                book = new Book(rs.getString("isbn"), rs.getString("title"), rs.getInt("editionNumber"), rs.getString("copyright"));

                String SQLAuthors = "SELECT a.* FROM authors a INNER JOIN authorisbn ai ON a.authorID = ai.authorID WHERE ai.isbn = ?";
                PreparedStatement stmtAuthors = connection.prepareStatement(SQLAuthors);
                stmtAuthors.setString(1, book.getIsbn());
                ResultSet rsAuthors = stmtAuthors.executeQuery();
                while (rsAuthors.next()) {
                    Author.AuthorDTO authorDTO = new Author.AuthorDTO(rsAuthors.getInt("authorID"), rsAuthors.getString("firstName"), rsAuthors.getString("lastName"));
                    book.getAuthorList().add(authorDTO);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        if (book != null) {
            return Response.ok(book).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No book found with ISBN: " + isbn).build();
        }
    }
    // URI for getting specific author
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/author/5
    @GET
    @Path("/author/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAuthorById(@PathParam("id") int id) {
        Author author = null;

        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "SELECT * FROM authors WHERE authorID = ?";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                author = new Author(rs.getInt("authorID"), rs.getString("firstName"), rs.getString("lastName"));

                String SQLBooks = "SELECT t.* FROM titles t INNER JOIN authorisbn ai ON t.isbn = ai.isbn WHERE ai.authorID = ?";
                PreparedStatement stmtBooks = connection.prepareStatement(SQLBooks);
                stmtBooks.setInt(1, author.getId());
                ResultSet rsBooks = stmtBooks.executeQuery();
                while (rsBooks.next()) {
                    Book.BookDTO bookDTO = new Book.BookDTO(
                            rsBooks.getString("isbn"),
                            rsBooks.getString("title"),
                            rsBooks.getInt("editionNumber"),
                            rsBooks.getString("copyright")
                    );
                    author.getBooks().add(bookDTO);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        if (author != null) {
            return Response.ok(author).build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("No author found with ID: " + id).build();
        }
    }
    // Retrieve all books associated with an author
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/author/5/books
    @GET
    @Path("/author/{id}/books")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksForAuthor(@PathParam("id") int authorId) {
        List<Book> books = new ArrayList<>();

        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "SELECT t.* FROM titles t INNER JOIN authorisbn ai ON t.isbn = ai.isbn WHERE ai.authorID = ?";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setInt(1, authorId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Book book = new Book(rs.getString("isbn"), rs.getString("title"), rs.getInt("editionNumber"), rs.getString("copyright"));

                String SQLAuthors = "SELECT a.* FROM authors a INNER JOIN authorisbn ai ON a.authorID = ai.authorID WHERE ai.isbn = ?";
                PreparedStatement stmtAuthors = connection.prepareStatement(SQLAuthors);
                stmtAuthors.setString(1, book.getIsbn());
                ResultSet rsAuthors = stmtAuthors.executeQuery();
                while (rsAuthors.next()) {
                    Author.AuthorDTO authorDTO = new Author.AuthorDTO(rsAuthors.getInt("authorID"), rsAuthors.getString("firstName"), rsAuthors.getString("lastName"));
                    book.getAuthorList().add(authorDTO);
                }

                books.add(book);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok(books).build();
    }
    // Add a book
    // Body,raw,JSON, in {}
    // "copyright": "2023",
    // "editionNumber": 1,
    // "isbn": "1234567890",
    // "title": "Sample Book"
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/addbook
    @POST
    @Path("/addbook")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBook(Book book) {
        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "INSERT INTO titles(isbn, title, editionNumber, copyright) VALUES(?, ?, ?, ?)";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setInt(3, book.getEditionNumber());
            stmt.setString(4, book.getCopyright());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok(book).build();
    }
    // Add an author
    // Body,raw,JSON in {}
    // "firstName": "Testing",
    // "id": 6,
    // "lastName": "AddAuthor"
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/addauthor
    @POST
    @Path("/addauthor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addAuthor(Author author) {
        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "INSERT INTO authors(firstName, lastName) VALUES(?, ?)";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok(author).build();
    }
    // Create link between book and author
    // Body,raw,JSON in {}
    // "authorId": "5",
    // "isbn": "0136151574"
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/associateauthor
    @POST
    @Path("/associateauthor")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response associateAuthorWithBook(Association association) {
        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "INSERT INTO authorisbn(authorID, isbn) VALUES(?, ?)";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setInt(1, association.getAuthorId());
            stmt.setString(2, association.getIsbn());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok("Association added.").build();
    }
    // Modify a book
    // Body,raw,JSON in {}
    // "copyright": "2023",
    // "editionNumber": 2023,
    // "isbn": "1234567890",
    // "title": "Modifying Book"
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/modbook
    @PUT
    @Path("/modbook")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modBook(Book book) {
        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "UPDATE titles SET title = ?, editionNumber = ?, copyright = ? WHERE isbn = ?";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, book.getTitle());
            stmt.setInt(2, book.getEditionNumber());
            stmt.setString(3, book.getCopyright());
            stmt.setString(4, book.getIsbn());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok(book).build();
    }
    // Modify an author
    // Body,raw,JSON
    // "firstName": "Testing",
    // "id": 6,
    // "lastName": "ModifyingAuthor"
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/modauthor
    @PUT
    @Path("/modauthor")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response modAuthor(Author author) {
        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "UPDATE authors SET firstName = ?, lastName = ? WHERE authorID = ?";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, author.getFirstName());
            stmt.setString(2, author.getLastName());
            stmt.setInt(3, author.getId());
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok(author).build();
    }
    // Delete a book
    // id is isbn
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library//delbook/1234567890
    @DELETE
    @Path("/delbook/{id}")
    public Response deleteBookByISBN(@PathParam("id") String isbn) {
        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "DELETE FROM titles WHERE isbn = ?";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setString(1, isbn);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok("Book with ISBN " + isbn + " was deleted.").build();
    }
    // Delete an author
    // http://localhost:8080/JavaRESTAPIProject-1.0-SNAPSHOT/api/library/delauthor/6
    @DELETE
    @Path("/delauthor/{id}")
    public Response deleteAuthorById(@PathParam("id") int id) {
        try (Connection connection = DBConnection.getBooksDatabaseConnection()) {
            String SQL = "DELETE FROM authors WHERE authorID = ?";
            PreparedStatement stmt = connection.prepareStatement(SQL);
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            ex.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Database error: " + ex.getMessage()).build();
        }

        return Response.ok("Author with ID " + id + " was deleted.").build();
    }

}