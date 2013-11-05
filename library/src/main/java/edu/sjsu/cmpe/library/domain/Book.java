package edu.sjsu.cmpe.library.domain;

import java.net.URL;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import com.fasterxml.jackson.annotation.JsonProperty;


public class Book
{
	@NotNull
	private long isbn;
	
	@NotEmpty
	private String title;
	
	@NotEmpty
	private String category;
	
	private URL coverimage;
	
	private enum Status
	{
		available,checkedout,inqueue,lost;
		
	}
	
	@JsonProperty
	private Status status=Status.available;
	
	public long getIsbn()
	{
		return this.isbn;
	}
	
	public void setIsbn(long isbn)
	{
		this.isbn=isbn;
	}
	
	public String getTitle() 
	{
	        return title;
	}

	public void setTitle(String title) 
	{
	        this.title = title;
	}


	public String getCategory() {
	        return category;
	    }

	
	public void setCategory(String category) {
	        this.category = category;
	    }

	public URL getCoverimage() {
	        return coverimage;
	    }

	public void setCoverimage(URL coverImage) {
	        this.coverimage = coverImage;
	    }
	    
	public void setStatus(String status)
	{
			this.status=Status.valueOf(status);
	}
	
	public String getStatus()
	{
		return this.status.name();
	}


}