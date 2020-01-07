import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import com.solacesystems.jms.SolConnectionFactory;
import com.solacesystems.jms.SolJmsUtility;

public class JMSPublisher {
    /**
     * A JMS Publisher class that can publish to certain messages to certain topics.
     */

    Session session;
    Topic topic;
    MessageProducer messageProducer;

    JMSPublisher(ConnectionData connectionData, String topicString) throws Exception {
        /**
         * Constructor that creates an instance of the Publisher class
         *
         * @param connectionData contains the necessary information to connect the publisher to the chosen broker
         * @param topicString is the topic that the publisher will publish to
         *
         */

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
        /**
         * Publishes a message to a specific topic
         *
         * @param messageString the message that will be published to the specific topic
         */

        // Create the message
        TextMessage message = this.session.createTextMessage(messageString);

        System.out.printf("Sending message '%s' to topic '%s'...%n", message.getText(), this.topic.toString());

        // Send the message
        // NOTE: JMS Message Priority is not supported by the Solace Message Bus
        this.messageProducer.send(this.topic, message, DeliveryMode.PERSISTENT,
                Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
    }

}
