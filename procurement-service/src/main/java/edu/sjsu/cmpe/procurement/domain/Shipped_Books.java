package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;

public class Shipped_Books
{
	private ArrayList<Book> shipped_books;
	
	public Shipped_Books()
	{
		shipped_books=new ArrayList<Book>();
	}
	
	public void setShipped_Books(ArrayList<Book> shipped_books)
	{
		this.shipped_books=shipped_books;
	}
	
	public ArrayList<Book> getShipped_Books()
	{
		return this.shipped_books;
	}
}