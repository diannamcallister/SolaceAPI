import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class JCSMPGetServiceAndPublish {

    /**
     * Overarching class that is used to get all services of a specified API Token, allow a user to choose a specific
     * service, instantiate a publisher and subscriber, allow the user to choose a specific topic and message, and
     * publish the message the specified topic with the publisher, then subscribers subscribed to the topic will
     * consume the message. This is the class associated with JCSMP Subscriber and Publisher.
     */

    public static void main(String... args) throws Exception {

        SolaceAPI api = new SolaceAPI(); // Create an instance of the solaceAPI class
        HashMap<String, String> all_services = api.get_all_services(); // Get all services of the solaceAPI instance

        // Prepare for user input by listing all services and prompting the user to chose a specific service
        System.out.println("The list of services to choose from are:");
        for (String key : all_services.keySet()) {
            System.out.println( key );
        }
        System.out.println("\n");
        System.out.println("Which service would you like to use today? Enter the name of one of the services listed above.");

        Scanner in = new Scanner(System.in);
        String chosen_service = in.next();

        // Check that the user chose an existing service and if not, let the user know and choose again
        while (! all_services.containsKey(chosen_service)) {
            System.out.println("That is not a valid service. Please try again and choose a valid service.");
            chosen_service = in.next();
        }

        ConnectionData service_data = api.get_service_by_ID(all_services.get(chosen_service));

        // Ask for user input of a topic to subscribe and publish to
        System.out.println("What topic would you like to subscribe and publish to?");
        String chosen_topic = in.next();

        // Create instances of both the subscriber and publisher that will each connect to the service broker
        //Subscriber subscriber = new Subscriber(service_data, chosen_topic);

        // Ask for user input of a topic to subscribe and publish to
        System.out.println("What message would you like to publish?");
        String chosen_message = in.next();
        chosen_message += in.nextLine();

        // Message that will be sent to from the publisher to the consumers subscribed to the topic above
        JCSMPSubscriber subscriber = new JCSMPSubscriber(service_data, chosen_topic);
        JCSMPPublisher publisher = new JCSMPPublisher(service_data, chosen_topic, chosen_message);

        // Allows the subscriber to consume the message before the function exits
        TimeUnit.SECONDS.sleep(5);

    }

}
