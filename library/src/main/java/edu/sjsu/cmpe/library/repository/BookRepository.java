package edu.sjsu.cmpe.library.repository;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import edu.sjsu.cmpe.library.domain.Book;

public class BookRepository implements BookRepositoryInterface
{
	private final ConcurrentHashMap<Long,Book> bookInMemoryMap;
	private long isbnKey;
	
	public BookRepository()
	{
		bookInMemoryMap=seedData();
		isbnKey=3;
	}
	
	private ConcurrentHashMap<Long,Book> seedData()
	{
		ConcurrentHashMap<Long,Book> bookMap=new ConcurrentHashMap<Long,Book>();
		Book book=new Book();
		book.setIsbn(1);
		book.setCategory("computer");
		book.setTitle("Java Concurrency in practice");
		try
		{
			book.setCoverimage(new URL("http://goo.gl/N96GJN"));
		}
		catch(MalformedURLException e)
		{
			
		}
		bookMap.put(book.getIsbn(), book);
		
		book=new Book();
		book.setIsbn(2);
		book.setCategory("computer");
		book.setTitle("Restful Web Services");
		try
		{
			book.setCoverimage(new URL("http://goo.gl/ZGmzoJ"));
		}
		catch(MalformedURLException e)
		{
			
		}
		bookMap.put(book.getIsbn(), book);
		return bookMap;
	}
	
	private final Long generateISBNkey()
	{
		return Long.valueOf(isbnKey++);
	}
	
	@Override
	public Book saveBook(Book newBook)
	{
		checkNotNull(newBook,"newBook instance must not be null");
		Long isbn=generateISBNkey();
		newBook.setIsbn(isbn);
		bookInMemoryMap.put(isbn, newBook);
		return newBook;
	}
	
	@Override
	public void insertBook(long isbn,Book newBook)
	{
		checkNotNull(newBook,"newBook instance must not be null");
		bookInMemoryMap.put(isbn, newBook);
	}
	

	@Override
	public Book getBookByISBN(Long isbn)
	{
		checkArgument(isbn>0,"ISBN was %s but expected greater than zero value",isbn);
		
		return bookInMemoryMap.get(isbn);
	}
	
	@Override
	public List<Book> getAllBooks()
	{
		return new ArrayList<Book>(bookInMemoryMap.values());
	}
	
	@Override
	public void delete(Long isbn)
	{
		bookInMemoryMap.remove(isbn);
	}
}