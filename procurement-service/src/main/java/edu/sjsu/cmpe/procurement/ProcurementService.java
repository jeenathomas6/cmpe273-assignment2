package edu.sjsu.cmpe.procurement;

import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;

import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.config.Bootstrap;
import com.yammer.dropwizard.config.Environment;

import edu.sjsu.cmpe.procurement.config.ProcurementServiceConfiguration;
import edu.sjsu.cmpe.procurement.domain.ProcurementConsumer;
import static org.quartz.JobBuilder.*;
import static org.quartz.TriggerBuilder.*;

import org.quartz.*;

import static org.quartz.SimpleScheduleBuilder.*;

public class ProcurementService extends Service<ProcurementServiceConfiguration>
{
	ProcurementConsumer consumer;
	
	public static void main(String[] args) throws Exception
	{
		new ProcurementService().run(args);
	}
	
	@Override
	public void initialize(Bootstrap<ProcurementServiceConfiguration> bootstrap)
	{
		bootstrap.setName("procurement-service");
	}
	
	@Override
	public void run(ProcurementServiceConfiguration configuration, Environment environment)
	{
		String queueName=configuration.getStompQueueName();
		String topicName=configuration.getStompTopicName();
		String user=configuration.getApolloUser();
		String password=configuration.getApolloPassword();
		String host=configuration.getApolloHost();
		String portString=configuration.getApolloPort();
		int port=Integer.parseInt(portString);
		
		
		//ApolloParameters ap=new ApolloParameters(queueName, topicName, user, password,host,port);
		SchedulerFactory sf = new StdSchedulerFactory();
		try {
			Scheduler sched = sf.getScheduler();
			JobDetail job=newJob(ProcurementConsumer.class)
					.withIdentity("pullRequests","group1")
					.usingJobData("queueName",queueName)
					.usingJobData("topicName",topicName)
					.usingJobData("user",user)
					.usingJobData("password",password)
					.usingJobData("host",host)
					.usingJobData("port", port)
					.build();
			
			Trigger trigger=newTrigger()
					.withIdentity("myTrigger","group1")
					.startNow()
					.withSchedule(simpleSchedule()
							.withIntervalInMinutes(5)
							.repeatForever())
					.forJob(job)
					.build();
			
			sched.scheduleJob(job, trigger);
			sched.start();
		//	Thread.sleep(90L*1000L);
			
		} catch (SchedulerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} /*catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} */
	}
}