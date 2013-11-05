package edu.sjsu.cmpe.library.api;

import javax.jms.JMSException;
import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.PathParam;




import com.yammer.dropwizard.jersey.params.LongParam;

import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;
import edu.sjsu.cmpe.library.domain.Book;
import edu.sjsu.cmpe.library.domain.LibraryProducer;
import edu.sjsu.cmpe.library.dto.*;

@Path("v1/books/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource
{
	private final BookRepositoryInterface bookRepository; 
	private String queueName;
	private String topicName;
	private String user;
	private String password;
	private String host;
	private String port;
	private String library_name;
	
	public BookResource(BookRepositoryInterface bookRepository,String queueName,String topicName,String user,String password,String host,String port,String library_name)
	{
		this.bookRepository=bookRepository;
		this.queueName=queueName;
		this.topicName=topicName;
		this.user=user;
		this.password=password;
		this.host=host;
		this.port=port;
		this.library_name=library_name;
	}
	
	@GET
	@Path("/{isbn}")
	public BookDto getBookByIsbn(@PathParam("isbn") LongParam isbn)
	{
		Book book=bookRepository.getBookByISBN(isbn.get());
		BookDto bookResponse=new BookDto(book);
		bookResponse.addLink(new LinkDto("view-book","/books/"+book.getIsbn(),"GET"));
		bookResponse.addLink(new LinkDto("update-book-status","/books/"+book.getIsbn(),"PUT"));
		return bookResponse;
	}
	
	@POST
	public Response createBook(@Valid Book request)
	{
		Book savedBook=bookRepository.saveBook(request);
		String location="/books/" + savedBook.getIsbn();
		BookDto bookResponse=new BookDto(savedBook);
		bookResponse.addLink(new LinkDto("view-book",location,"GET"));
		bookResponse.addLink(new LinkDto("update-book-status",location,"PUT"));
		return Response.status(201).entity(bookResponse).build();
	}
	
	@GET
	@Path("/")
	public BooksDto getAllBooks()
	{
		BooksDto bookResponse=new BooksDto(bookRepository.getAllBooks());
		bookResponse.addLink(new LinkDto("create-book","/books","POST"));
		return bookResponse;
	}
	
	@PUT
	@Path("/{isbn}")
	public Response updateBookStatus(@PathParam("isbn") LongParam isbn,@DefaultValue("available") @QueryParam("status") String status)
	{
		Book book=bookRepository.getBookByISBN(isbn.get());
		book.setStatus(status.toLowerCase().trim());
		BookDto bookResponse=new BookDto(book);
		bookResponse.addLink(new LinkDto("view-book","/books/"+book.getIsbn(),"GET"));
		
		if(status.equals("lost"))
		{
			LibraryProducer lb=new LibraryProducer(queueName,topicName,user,password,host,port,library_name);
			try {
				lb.requestToBroker(isbn.get());
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
		return Response.status(200).entity(bookResponse).build();
	}
	
	@DELETE
	@Path("/{isbn}")
	public BookDto deleteBook(@PathParam("isbn") LongParam isbn)
	{
		bookRepository.delete(isbn.get());
		BookDto bookResponse=new BookDto(null);
		bookResponse.addLink(new LinkDto("create-book","/books","POST"));
		return bookResponse;
	}
	
}