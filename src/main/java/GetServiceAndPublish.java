import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;

public class GetServiceAndPublish {

    public static void main(String... args) throws Exception {

        SolaceAPI api = new SolaceAPI();
        HashMap<String, String> all_services = api.get_all_services();

        System.out.println("The list of services to choose from are:");
        for (String key : all_services.keySet()) {
            System.out.println( key );
        }
        System.out.println("\n");
        System.out.println("Which service would you like to use today?");

        Scanner in = new Scanner(System.in);
        String chosen_service = in.next();


        while (! all_services.containsKey(chosen_service)) {
            System.out.println("That is not a valid service. Please try again and choose a valid service.");
            chosen_service = in.next();
        }

        ConnectionData service_data = api.get_service_by_ID(all_services.get(chosen_service));

        String topicString = "try_me";

        Subscriber subscriber = new Subscriber(service_data, topicString);
        Publisher publisher = new Publisher(service_data, topicString);

        publisher.sendMessage("hellooooooooo");

        //without this, function will exit so quickly that subscriber cannot consume the message
        TimeUnit.SECONDS.sleep(5);

    }

}
