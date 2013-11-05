package edu.sjsu.cmpe.library.config;

import org.hibernate.validator.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.yammer.dropwizard.config.Configuration;

public class LibraryServiceConfiguration extends Configuration
{
	@NotEmpty
	@JsonProperty
	private String stompQueueName;
	
	@NotEmpty
	@JsonProperty
	private String stompTopicName;
	
	@NotEmpty
	@JsonProperty
	private String apolloUser;
	
	@NotEmpty
	@JsonProperty
	private String apolloPassword;
	
	@NotEmpty
	@JsonProperty
	private String apolloHost;
	
	@NotEmpty
	@JsonProperty
	private String apolloPort;
	
	@NotEmpty
	@JsonProperty
	private String library_name;
	
	public String getStompQueueName()
	{
		return this.stompQueueName;
	}
	
	public String getStompTopicName()
	{
		return this.stompTopicName;
	}
	
	public String getApolloUser()
	{
		return this.apolloUser;
	}
	
	public String getApolloPassword()
	{
		return this.apolloPassword;
	}
	
	public String getApolloHost()
	{
		return this.apolloHost;
	}
	
	public String getApolloPort()
	{
		return this.apolloPort;
	}
	
	public String getLibrary_Name()
	{
		return this.library_name;
	}
	
	public void setStompQueueName(String stompQueueName)
	{
		this.stompQueueName=stompQueueName;
	}
	
	public void setStompTopicName(String stompTopicName)
	{
		this.stompTopicName=stompTopicName;
	}
	
	public void setApolloUser(String apolloUser)
	{
		this.apolloUser=apolloUser;
	}
	
	public void setApolloPassword(String apolloPassword)
	{
		this.apolloPassword=apolloPassword;
	}
	
	public void setApolloHost(String apolloHost)
	{
		this.apolloHost=apolloHost;
	}
	
	public void setApolloPort(String apolloPort)
	{
		this.apolloPort=apolloPort;
	}
	
	public void setLibrary_Name(String library_name)
	{
		this.library_name=library_name;
	}
}