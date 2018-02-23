package com.db2db;

import com.microsoft.azure.eventprocessorhost.*;

import java.util.concurrent.ExecutionException;

import com.microsoft.azure.eventhubs.EventData;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
    	 final String consumerGroupName = System.getenv("ConsumerGroup");
         
         final String eventHubName = "hub01";
         final String storageConnectionString = "DefaultEndpointsProtocol=https;AccountName=stodb2db;AccountKey=4Kr/oRjEEXnWgMlqYPWVnnNnajinN63Alr7hJ1ZdKsi+UXfM7hOg9E1nFakxCkT6/eG6qJ6w5Zev0Ji9PID5tQ==;EndpointSuffix=core.windows.net";

         final String eventHubConnectionString = "Endpoint=sb://db2dbhub.servicebus.windows.net/;SharedAccessKeyName=SendListen;SharedAccessKey=iKp5jMn8D4ccWux89c6S1n5+CZAtqpZckLJ0G7Qw/+0=;EntityPath=hub01";

         EventProcessorHost host = new EventProcessorHost(eventHubName, consumerGroupName, eventHubConnectionString, storageConnectionString);

         System.out.println("Registering host named " + host.getHostName());
         EventProcessorOptions options = new EventProcessorOptions();
         options.setExceptionNotification(new ErrorNotificationHandler());
         try
         {
             host.registerEventProcessor(EventProcessor.class, options).get();
         }
         catch (Exception e)
         {
             System.out.print("Failure while registering: ");
             if (e instanceof ExecutionException)
             {
                 Throwable inner = e.getCause();
                 System.out.println(inner.toString());
             }
             else
             {
                 System.out.println(e.toString());
             }
         }

         System.out.println("Press enter to stop");
         try
         {
             System.in.read();
             host.unregisterEventProcessor();

             System.out.println("Calling forceExecutorShutdown");
             EventProcessorHost.forceExecutorShutdown(120);
         }
         catch(Exception e)
         {
             System.out.println(e.toString());
             e.printStackTrace();
         }

         System.out.println("End of sample");
     }
    
}
