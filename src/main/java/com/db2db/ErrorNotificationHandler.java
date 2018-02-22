package com.db2db;
import java.util.function.Consumer;
import com.microsoft.azure.eventprocessorhost.ExceptionReceivedEventArgs;

public class ErrorNotificationHandler implements Consumer<ExceptionReceivedEventArgs>
{
    public void accept(ExceptionReceivedEventArgs t)
    {
        System.out.println("SAMPLE: Host " + t.getHostname() + " received general error notification during " + t.getAction() + ": " + t.getException().toString());
    }
}