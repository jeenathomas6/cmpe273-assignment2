package edu.sjsu.cmpe.library.domain;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.fusesource.stomp.jms.StompJmsConnectionFactory;
import org.fusesource.stomp.jms.StompJmsDestination;

import edu.sjsu.cmpe.library.config.LibraryServiceConfiguration;

public class LibraryProducer
{
	private String queueName;
	private String topicName;
	private String user;
	private String password;
	private String host;
	private int port;
	private String library_name;
	
	public LibraryProducer(String queueName,String topicName,String user,String password,String host,String port,String library_name)
	{
		this.queueName=queueName;
		this.topicName=topicName;
		this.user=user;
		this.password=password;
		this.host=host;
		this.port=Integer.parseInt(port);
		this.library_name=library_name;
	}
	
	public void requestToBroker(Long isbn) throws JMSException
	{
		StompJmsConnectionFactory factory=new StompJmsConnectionFactory();
		factory.setBrokerURI("tcp://"+host+":"+port);
		
		Connection connection=factory.createConnection(user,password);
		connection.start();
		
		Session session=connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination dest=session.createQueue(queueName);
		//Destination dest=session.createQueue("30787.book.orders");
		//Destination dest=new StompJmsDestination("/queue/30787.book.orders");
		MessageProducer producer=session.createProducer(dest);
		producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
		
		String data=library_name+":"+isbn;
		TextMessage msg=session.createTextMessage(data);
		msg.setLongProperty("id", System.currentTimeMillis());
		producer.send(msg);
		
		connection.close();
	}
}