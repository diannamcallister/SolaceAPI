

import java.util.concurrent.CountDownLatch;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.JCSMPFactory;
import com.solacesystems.jcsmp.JCSMPProperties;
import com.solacesystems.jcsmp.JCSMPSession;
import com.solacesystems.jcsmp.TextMessage;
import com.solacesystems.jcsmp.Topic;
import com.solacesystems.jcsmp.XMLMessageConsumer;
import com.solacesystems.jcsmp.XMLMessageListener;

public class JCSMPSubscriber {
    /**
     * A JCSMP Subscriber class that can subscribe to certain topics and consume messages from a specific broker.
     */

    Topic topic;
    JCSMPSession session;

    JCSMPSubscriber(ConnectionData connectionData, String topicString) throws JCSMPException {
        /**
         * Constructor that creates an instance of the Subscriber class
         *
         * @param connectionData contains the necessary information to connect the subscriber to the chosen broker
         * @param topicString is the topic that the subscriber will subscribe to
         *
         */


        System.out.println("TopicSubscriber initializing...");
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, connectionData.getHost());     // host:port
        properties.setProperty(JCSMPProperties.USERNAME, connectionData.getUsername()); // client-username
        properties.setProperty(JCSMPProperties.VPN_NAME,  connectionData.getVpnName()); // message-vpn
        properties.setProperty(JCSMPProperties.PASSWORD, connectionData.getPassword()); // client-password

        this.topic = JCSMPFactory.onlyInstance().createTopic(topicString);
        this.session = JCSMPFactory.onlyInstance().createSession(properties);

        this.session.connect();

        final CountDownLatch latch = new CountDownLatch(1); // used for
        // synchronizing b/w threads
        /** Anonymous inner-class for MessageListener
         *  This demonstrates the async threaded message callback */
        final XMLMessageConsumer cons = this.session.getMessageConsumer(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                if (msg instanceof TextMessage) {
                    System.out.printf("TextMessage received: '%s'%n",
                            ((TextMessage)msg).getText());
                } else {
                    System.out.println("Message received.");
                }
                System.out.printf("Message Dump:%n%s%n",msg.dump());
                latch.countDown();  // unblock main thread
            }

            @Override
            public void onException(JCSMPException e) {
                System.out.printf("Consumer received exception: %s%n",e);
                latch.countDown();  // unblock main thread
            }
        });
        session.addSubscription(this.topic);
        System.out.println("Connected. Awaiting message...");
        cons.start();
        // Consume-only session is now hooked up and running!
    }
}