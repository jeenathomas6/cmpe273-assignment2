package edu.sjsu.cmpe.library.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import edu.sjsu.cmpe.library.domain.Book;

@JsonPropertyOrder(alphabetic = true)
public class BookDto extends LinksDto {
    private Book book;

 
    public BookDto(Book book) {
        super();
        this.book = book;
    }

    public Book getBook() {
        return book;
    }


    public void setBook(Book book) {
        this.book = book;
    }
}