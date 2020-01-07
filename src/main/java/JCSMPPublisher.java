import com.solacesystems.jcsmp.*;


//    public static void main(String... args) throws JCSMPException {

public class JCSMPPublisher {
    /**
     * A JCSMP Publisher class that can publish to certain messages to certain topics.
     */

    Topic topic;
    JCSMPSession session;

    JCSMPPublisher(ConnectionData connectionData, String topicString, String messageString) throws Exception {
        /**
         * Constructor that creates an instance of the Publisher class
         *
         * @param connectionData contains the necessary information to connect the publisher to the chosen broker
         * @param topicString is the topic that the publisher will publish to
         *
         */

        System.out.printf("TopicPublisher is connecting to Solace messaging at %s...%n", connectionData.getHost());


        // Create a JCSMP Session
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, connectionData.getHost());     // host:port
        properties.setProperty(JCSMPProperties.USERNAME, connectionData.getUsername()); // client-username
        properties.setProperty(JCSMPProperties.VPN_NAME,  connectionData.getVpnName()); // message-vpn
        properties.setProperty(JCSMPProperties.PASSWORD, connectionData.getPassword()); // client-password

        this.session =  JCSMPFactory.onlyInstance().createSession(properties);

        this.session.connect();

        this.topic = JCSMPFactory.onlyInstance().createTopic(topicString);


        /** Anonymous inner-class for handling publishing events */
        XMLMessageProducer prod = this.session.getMessageProducer(new JCSMPStreamingPublishEventHandler() {
            @Override
            public void responseReceived(String messageID) {
                System.out.println("Producer received response for msg: " + messageID);
            }
            @Override
            public void handleError(String messageID, JCSMPException e, long timestamp) {
                System.out.printf("Producer received error for msg: %s@%s - %s%n",
                        messageID,timestamp,e);
            }
        });
        // Publish-only session is now hooked up and running!

        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setText(messageString);
        System.out.printf("Connected. About to send message '%s' to topic '%s'...%n",messageString,topic.getName());
        prod.send(msg,topic);
    }
}
