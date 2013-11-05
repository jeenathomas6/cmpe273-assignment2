package edu.sjsu.cmpe.procurement.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;

import javassist.bytecode.Descriptor.Iterator;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueBrowser;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;
import org.fusesource.stomp.jms.message.StompJmsMessage;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class ProcurementConsumer implements Job
{
	public ProcurementConsumer()
	{
		
	}
	
	public void execute(JobExecutionContext context)
		      throws JobExecutionException
	{
		ArrayList<String> bookRequests=new ArrayList<String>();
		Shipped_Books shipBooks=new Shipped_Books();
		
		JobKey key=context.getJobDetail().getKey();
		JobDataMap dataMap=context.getJobDetail().getJobDataMap();
		String queueName=dataMap.getString("queueName");
		String topicName=dataMap.getString("topicName");
		String user=dataMap.getString("user");
		String password=dataMap.getString("password");
		String host=dataMap.getString("host");
		//String port=dataMap.getString("port");
		
		StompJmsConnectionFactory factory=new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://"+host+":"+"61613");
		//factory.setBrokerURI("tcp://"+host+":"+port);
		try
		{
			Connection connection=factory.createConnection(user,password);
			connection.start();
			Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Queue queue=session.createQueue("30787.book.orders");
			Destination dest=queue;
			//Destination dest = new StompJmsDestination("/queue/30787.book.orders");
			MessageConsumer consumer=session.createConsumer(dest);
			QueueBrowser qb=session.createBrowser(queue);
			Enumeration e=qb.getEnumeration();	
			while(e.hasMoreElements())
			{
		    	//Message msg=consumer.receive();
				Message msg=(Message)e.nextElement();
				msg=consumer.receive();
		   		if(msg instanceof TextMessage)
				{
					String body=((TextMessage)msg).getText();
					/*if(body.equals("SHUTDOWN"))
					{
						break;
					}*/
					bookRequests.add(body);
					System.out.println(body);
				}
				
				else if(msg instanceof StompJmsMessage)
				{
					StompJmsMessage smsg=((StompJmsMessage)msg);
					String body=smsg.getFrame().contentAsString();
					/*if(body.equals("SHUTDOWN"))
					{
						break;
					}*/
					bookRequests.add(body);
					System.out.println(body);
				}
				else
				{
					System.out.println("Recieved message= "+msg.getClass());
				}
			}
			
			qb.close();
			connection.close();
			
			//do post 
			BookOrders bookOrders=new BookOrders();
			for(int i=0;i<bookRequests.size();i++)
			{
				String value=bookRequests.get(i);
				String[] parts=value.split(":");
				bookOrders.getOrder_Book_Isbns().add(Integer.parseInt(parts[1]));
				System.out.println(bookOrders.getOrder_Book_Isbns().get(i));
			}
			
			Client client=Client.create();
			System.out.println("Client created");
			WebResource webResource=client.resource("http://54.215.210.214:9000/orders");
			System.out.println("WebResource created");
			ClientResponse response=webResource.type("application/json")
					.post(ClientResponse.class,bookOrders);
			System.out.println("Response recieved");
			String output=response.getEntity(String.class);
			System.out.println(output);
			
			//Do Http get
			WebResource webResource1=client.resource("http://54.215.210.214:9000/orders/30787");
			ClientResponse response1=webResource1.accept("application/json").get(ClientResponse.class);
			String output1=response1.getEntity(String.class);
			System.out.println(output1.toString());
			
			//Parse response
			
			
			
			ArrayList<String> computerMessages=new ArrayList<String>();
			ArrayList<String> comicsMessages=new ArrayList<String>();
			ArrayList<String> managementMessages=new ArrayList<String>();
			ArrayList<String> siMessages=new ArrayList<String>();
			try {
				JSONObject obj=new JSONObject(output1);
				JSONArray jsonMainArray=obj.getJSONArray("shipped_books");
				for(int i=0;i<jsonMainArray.length();i++)
				{
					Book newBook=new Book();
					JSONObject childObj=jsonMainArray.getJSONObject(i);
					String category=childObj.getString("category");
					System.out.println(category);
					String coverimage=childObj.getString("coverimage");
					System.out.println(coverimage);
					int isbn=childObj.getInt("isbn");
					System.out.println(isbn);
					String title=childObj.getString("title");
					System.out.println(title);
					newBook.setCategory(category);
					newBook.setCoverimage(coverimage);
					newBook.setIsbn(isbn);
					newBook.setTitle(title);
					shipBooks.getShipped_Books().add(newBook);
					if(category.equals("computer"))
					{
						computerMessages.add(isbn+":"+title+":"+category+":"+coverimage);
					
					}
					else if(category.equals("comics"))
					{
						comicsMessages.add(isbn+":"+title+":"+category+":"+coverimage);
					}
					else if(category.equals("management"))
					{
						managementMessages.add(isbn+":"+title+":"+category+":"+coverimage);
					}
					else
					{
						siMessages.add(isbn+":"+title+":"+category+":"+coverimage);
					}
				
				}
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			//Publisher code
			Connection connection1=factory.createConnection(user,password);
			connection1.start();
			Session pubSession=connection1.createSession(false, Session.AUTO_ACKNOWLEDGE);
			
			for(int i=0;i<computerMessages.size();i++)
			{
				if(computerMessages.size()>0){
			    Destination computerDest=new StompJmsDestination("/topic/30787.book.computer");
				MessageProducer producer=pubSession.createProducer(computerDest);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				String data=computerMessages.get(i);
				TextMessage msg=pubSession.createTextMessage(data);
				System.out.println(msg);
				msg.setLongProperty("id",System.currentTimeMillis());
				producer.send(msg);
				System.out.println("Message sent");
				}
			}
			
			for(int i=0;i<comicsMessages.size();i++)
			{
				if(comicsMessages.size()>0){
				Destination comicDest=new StompJmsDestination("/topic/30787.book.comics");
				MessageProducer producer=pubSession.createProducer(comicDest);
				producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				String data=comicsMessages.get(i);
				TextMessage msg=pubSession.createTextMessage(data);
				msg.setLongProperty("id",System.currentTimeMillis());
				producer.send(msg);
				System.out.println("Message sent");
				}
			}
			
			
			for(int i=0;i<managementMessages.size();i++)
			{
				if(managementMessages.size()>0){
				Destination mgmtDest=new StompJmsDestination("/topic/30787.book.management");
				MessageProducer producer3=pubSession.createProducer(mgmtDest);
				producer3.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				String data=managementMessages.get(i);
				TextMessage msg=pubSession.createTextMessage(data);
				msg.setLongProperty("id",System.currentTimeMillis());
				producer3.send(msg);
				System.out.println("Message sent");
				}
			}
			
			
			for(int i=0;i<siMessages.size();i++)
			{
				if(siMessages.size()>0){
				Destination siDest=new StompJmsDestination("/topic/30787.book.selfimprovement");
				MessageProducer producer4=pubSession.createProducer(siDest);
				producer4.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
				String data=siMessages.get(i);
				TextMessage msg=pubSession.createTextMessage(data);
				System.out.print(msg);
				msg.setLongProperty("id",System.currentTimeMillis());
				producer4.send(msg);
				System.out.println("Message sent");
				}
			}
			connection1.close();
			computerMessages.clear();
			comicsMessages.clear();
			managementMessages.clear();
			siMessages.clear();
			
		}
		catch(JMSException e)
		{
			
		}
		
		//System.out.println("Hello World");
	}
}