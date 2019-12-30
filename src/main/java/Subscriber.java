import java.util.concurrent.CountDownLatch;

import javax.jms.*;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

public class Subscriber {
    /**
     * A Subscriber class that can subscribe to certain topics and consume messages from a specific broker.
     */

    Session session;
    Topic topic;
    MessageConsumer messageConsumer;
    // Latch used for synchronizing between threads
    final CountDownLatch latch = new CountDownLatch(1);

    Subscriber(ConnectionData connectionData, String topicString) throws Exception {
        /**
         * Constructor that creates an instance of the Subscriber class
         *
         * @param connectionData contains the necessary information to connect the subscriber to the chosen broker
         * @param topicString is the topic that the subscriber will subscribe to
         *
         */

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

    }

}
