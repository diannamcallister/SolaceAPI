import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class GetServiceAndPublish {

    public static void main(String... args) throws Exception {

        SolaceAPI api = new SolaceAPI();
        HashMap<String, String> all_services = api.get_all_services();
        ConnectionData service_data = api.get_service_by_ID(all_services.get("Dianna"));

        String topicString = "try_me";

//        Function<String, Void> printMessage = msg -> {System.out.println(msg);
//        return null;
//        };

        Subscriber subscriber = new Subscriber(service_data, topicString);
        Publisher publisher = new Publisher(service_data, topicString);

        publisher.sendMessage("hellooooooooo");

        //without this, function will exit so quickly that subscriber cannot consume the message
        TimeUnit.SECONDS.sleep(5);

    }

}
