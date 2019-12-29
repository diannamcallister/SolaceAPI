//package com.solace.samples;

import java.util.concurrent.CountDownLatch;

import javax.jms.*;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

public class Subscriber {

    Session session;
    Topic topic;
    MessageConsumer messageConsumer;
    // Latch used for synchronizing between threads
    final CountDownLatch latch = new CountDownLatch(1);

    Subscriber(ConnectionData connectionData, String topicString) throws Exception {

        System.out.printf("TopicSubscriber is connecting to Solace messaging at %s...%n", connectionData.getHost());

        // Programmatically create the connection factory using default settings
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(connectionData.getHost());
        connectionFactory.setVPN(connectionData.getVpnName());
        connectionFactory.setUsername(connectionData.getUsername());
        connectionFactory.setPassword(connectionData.getPassword());
        Connection connection = connectionFactory.createConnection();

        // Create a non-transacted, Auto ACK session.
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.printf("Connected to Solace Message VPN '%s' with client username '%s'.%n", connectionData.getVpnName(),
                connectionData.getUsername());

        // Create the subscription topic programmatically
        this.topic = session.createTopic(topicString);

        // Create the message consumer for the subscription topic
        this.messageConsumer = this.session.createConsumer(topic);


        // Use the anonymous inner class for receiving messages asynchronously
        messageConsumer.setMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message) {
                try {
                    //callback(message);
                    if (message instanceof TextMessage) {
                        System.out.printf("TextMessage received: '%s'%n", ((TextMessage) message).getText());
                    } else {
                        System.out.println("Message received.");
                    }
                    System.out.printf("Message Content:%n%s%n", SolJmsUtility.dumpMessage(message));
                    latch.countDown(); // unblock the main thread
                } catch (JMSException ex) {
                    System.out.println("Error processing incoming message.");
                    ex.printStackTrace();
                }
            }
        });

        // Start receiving messages
        connection.start();
        System.out.println("Awaiting message...");
        // the main thread blocks at the next statement until a message received
        //latch.await();

        //connection.stop();
        // Close everything in the order reversed from the opening order
        // NOTE: as the interfaces below extend AutoCloseable,
        // with them it's possible to use the "try-with-resources" Java statement
        // see details at https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        //messageConsumer.close();
        //session.close();
        //connection.close();

    }

}
