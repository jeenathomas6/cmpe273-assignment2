package edu.sjsu.cmpe.library.domain;

import java.net.MalformedURLException;
import java.net.URL;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;

import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;


public class LibrarySubscriber implements Runnable
{
	private String topicName;
	private String user;
	private String password;
	private String host;
	private int port;
	StompJmsConnectionFactory factory;
	Connection connection;
	Session session;
	Destination dest;
	MessageConsumer consumer;
	private final BookRepositoryInterface repo;
	
	public LibrarySubscriber(String topicName, String user, String password, String host, String port,BookRepositoryInterface repo)
	{
		this.repo=repo;
		this.topicName=topicName;
		this.user=user;
		this.password=password;
		this.host=host;
		this.port=Integer.parseInt(port);
		this.factory=new StompJmsConnectionFactory();
		this.factory.setBrokerURI("tcp://"+host+":"+port);
		try{
		this.connection=factory.createConnection(user,password);
		connection.start();
		this.session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		this.dest=new StompJmsDestination(this.topicName);
		
		consumer=session.createConsumer(dest);
		//consumer.setMessageListener(this);
		System.currentTimeMillis();
		System.out.println("Subscriber listening");
		}
		catch(JMSException e)
		{
			
		}
	}
	
	public void run()
	{
		
		while(true)
			{
				
				try 
				{
					Message message = consumer.receive();
				
					if(message instanceof TextMessage)
					{
						String body=((TextMessage)message).getText();
						if(body.equals("SHUTDOWN"))
						{
							break;
						}
						System.out.println(body);
						
						String[] parts=body.split(":",4);
						long isbn=Long.parseLong(parts[0]);
						String title=parts[1];
						String category=parts[2];
						URL coverimage=new URL(parts[3]);
						if(repo.getBookByISBN(isbn)==null)
						{
							Book newBook=new Book();
							newBook.setIsbn(isbn);
							newBook.setTitle(title);
							newBook.setCategory(category);
							newBook.setCoverimage(coverimage);
							repo.insertBook(isbn, newBook);
							System.out.println("Book Inserted");
						}
						else
						{
							Book book=repo.getBookByISBN(isbn);
							if(book.getStatus().equals("lost"))
							{
								book.setStatus("available");
								System.out.println("Status changed to available");
							}
						}
						
					}
				else if(message instanceof StompJmsMessage)
				{
					StompJmsMessage smsg=((StompJmsMessage)message);
					String body=smsg.getFrame().contentAsString();
					if(body.equals("SHUTDOWN"))
					{
						break;
					}
					System.out.println(body);
				}
				else
				{
					System.out.println("Unexpected message type: "+message.getClass());
				}
			
					
				
			}
				
			catch(JMSException e)
			{
				
			
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
				
			
		}
		try {
			connection.close();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
	}
}