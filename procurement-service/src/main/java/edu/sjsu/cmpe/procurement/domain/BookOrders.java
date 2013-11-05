package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BookOrders
{
	@JsonProperty
	private String id="30787";
	
	@JsonProperty
	private ArrayList<Integer> order_book_isbns;
	
	public BookOrders()
	{
		order_book_isbns=new ArrayList<Integer>();
	}
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id=id;
	}
	
	public void setOrder_Book_Isbns(ArrayList<Integer> order_book_isbns)
	{
		this.order_book_isbns=order_book_isbns;
	}
	
	public ArrayList<Integer> getOrder_Book_Isbns()
	{
		return this.order_book_isbns;
	}
	
}