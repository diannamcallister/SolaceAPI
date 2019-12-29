import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

public class Publisher {

    Session session;
    Topic topic;
    MessageProducer messageProducer;

    Publisher(ConnectionData connectionData, String topicString) throws Exception {

        System.out.printf("TopicPublisher is connecting to Solace messaging at %s...%n", connectionData.getHost());

        // Programmatically create the connection factory using default settings
        SolConnectionFactory connectionFactory = SolJmsUtility.createConnectionFactory();
        connectionFactory.setHost(connectionData.getHost());
        connectionFactory.setVPN(connectionData.getVpnName());
        connectionFactory.setUsername(connectionData.getUsername());
        connectionFactory.setPassword(connectionData.getPassword());

        // Create connection to the Solace router
        Connection connection = connectionFactory.createConnection();

        // Create a non-transacted, auto ACK session.
        this.session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        System.out.printf("Connected to the Solace Message VPN '%s' with client username '%s'.%n", connectionData.getVpnName(),
                connectionData.getUsername());

        // Create the publishing topic programmatically
        this.topic = session.createTopic(topicString);

        // Create the message producer for the created topic
        this.messageProducer = session.createProducer(topic);

    }

    public void sendMessage(String messageString) throws Exception {

        // Create the message
        TextMessage message = this.session.createTextMessage(messageString);

        System.out.printf("Sending message '%s' to topic '%s'...%n", message.getText(), this.topic.toString());

        // Send the message
        // NOTE: JMS Message Priority is not supported by the Solace Message Bus
        this.messageProducer.send(this.topic, message, DeliveryMode.PERSISTENT,
                Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);

        // Close everything in the order reversed from the opening order
        // NOTE: as the interfaces below extend AutoCloseable,
        // with them it's possible to use the "try-with-resources" Java statement
        // see details at https://docs.oracle.com/javase/tutorial/essential/exceptions/tryResourceClose.html
        //messageProducer.close();
        //session.close();
        //connection.close();
    }

}
