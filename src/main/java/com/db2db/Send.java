package com.db2db;
/*
 * Copyright (c) Microsoft. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.EventHubClient;
import com.microsoft.azure.eventhubs.PartitionSender;
import com.microsoft.azure.eventhubs.EventHubException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.function.BiConsumer;

public class Send {

    public static void main(String[] args)
            throws EventHubException, ExecutionException, InterruptedException, IOException {

        final String eventHubName = "hub01";
        final String connStr = "Endpoint=sb://db2dbhub.servicebus.windows.net/;SharedAccessKeyName=SendListen;SharedAccessKey=iKp5jMn8D4ccWux89c6S1n5+CZAtqpZckLJ0G7Qw/+0=;EntityPath=hub01";

        final Gson gson = new GsonBuilder().create();

        final PayloadEvent payload = new PayloadEvent(1);
        byte[] payloadBytes = gson.toJson(payload).getBytes(Charset.defaultCharset());
        final EventData sendEvent = new EventData(payloadBytes);

        final EventHubClient ehClient = EventHubClient.createFromConnectionStringSync(connStr.toString());;
        PartitionSender sender = null;

        try {
            // senders
            // Type-1 - Basic Send - not tied to any partition
//            ehClient.send(sendEvent).get();

            // Advanced Sends
            // Type-2 - Send using PartitionKey - all Events with Same partitionKey will land on the Same Partition
            final String partitionKey = "partitionTheStream";
            ehClient.sendSync(sendEvent, partitionKey);

            // Type-3 - Send to a Specific Partition
//            sender = ehClient.createPartitionSenderSync("1");
//            sender.sendSync(sendEvent);

            System.out.println(Instant.now() + ": Send Complete...");
            System.in.read();
        } finally {
            if (sender != null)
                sender.close().whenComplete(new BiConsumer<Void, Throwable>() {
                    public void accept(Void t, Throwable u) {
                        if (u != null) {
                            // wire-up this error to diagnostics infrastructure
                            System.out.println(String.format("closing failed with error: %s", u.toString()));
                        }
                        try {
                            ehClient.closeSync();
                        } catch (EventHubException sbException) {
                            // wire-up this error to diagnostics infrastructure
                            System.out.println(String.format("closing failed with error: %s", sbException.toString()));
                        }
                    }
                }).get();
            else if (ehClient != null)
                ehClient.closeSync();
        }
    }

    /**
     * actual application-payload, ex: a telemetry event
     */
    static final class PayloadEvent {
        PayloadEvent(final int seed) {
            this.id = "telemetryEvent1-critical-eventid中文-2345" + seed;
            this.strProperty = "This is a sample payloadEvent, which could be wrapped using eventdata and sent to eventhub." +
                    " None of the payload event properties will be looked-at by EventHubs client or Service." +
                    " As far as EventHubs service/client is concerted, it is plain bytes being sent as 1 Event.";
            this.longProperty = seed * new Random().nextInt(seed);
            this.intProperty = seed * new Random().nextInt(seed);
        }

        public String id;
        public String strProperty;
        public long longProperty;
        public int intProperty;
    }
}
