package edu.sjsu.cmpe.library.repository;

import java.util.List;

import edu.sjsu.cmpe.library.domain.Book;

public interface BookRepositoryInterface
{
	Book saveBook(Book newBook);
	
	Book getBookByISBN(Long isbn);
	
	List<Book> getAllBooks();
	
	void delete(Long isbn);
	
	public void insertBook(long isbn,Book newBook);
}