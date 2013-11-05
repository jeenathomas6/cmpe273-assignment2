package edu.sjsu.cmpe.library;

import javax.jms.JMSException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.sjsu.cmpe.library.api.BookResource;
import edu.sjsu.cmpe.library.api.RootResource;
import edu.sjsu.cmpe.library.config.*;
import edu.sjsu.cmpe.library.domain.LibrarySubscriber;
import edu.sjsu.cmpe.library.repository.BookRepository;
import edu.sjsu.cmpe.library.repository.BookRepositoryInterface;

public class LibraryService extends Service<LibraryServiceConfiguration>
{
	private final Logger log=LoggerFactory.getLogger(getClass());
	
	public static void main(String[] args) throws Exception
	{
		new LibraryService().run(args);
	}
	
	@Override
	public void initialize(Bootstrap<LibraryServiceConfiguration> bootstrap)
	{
		bootstrap.setName("library-service");
	}
	
	@Override
	public void run(LibraryServiceConfiguration configuration, Environment environment)
	{
		String queueName=configuration.getStompQueueName();
		String topicName=configuration.getStompTopicName();
		String user=configuration.getApolloUser();
		String password=configuration.getApolloPassword();
		String host=configuration.getApolloHost();
		String port=configuration.getApolloPort();
		String library_name=configuration.getLibrary_Name();
		
		environment.addResource(RootResource.class);
		BookRepositoryInterface bookRepository=new BookRepository();
		environment.addResource(new BookResource(bookRepository,queueName,topicName,user,password,host,port,library_name));
		
		int numThreads=1;
		ExecutorService executor=Executors.newFixedThreadPool(numThreads);
		//LibrarySubscriber subscriber=new LibrarySubscriber(topicName,user,password,host,port);
		Runnable backgroundTask=new LibrarySubscriber(topicName,user,password,host,port,bookRepository);
		System.out.println("About to submit background task");
		try
		{
			
			executor.execute(backgroundTask);
		}
		catch(Exception e)
		{
			executor.shutdown();
			e.printStackTrace();
		}
		/*finally
		{
			executor.shutdown();
		}*/
	     
		
		
		/*try {
			subscriber.responseFromBroker();
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}
}