import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class SolaceAPI {
    /**
     * Performs multiple HTTP GET requests to communicate with Solace Cloud to get all services under a specific API
     * token, as well as more detailed information about a specific service under that same API token.
     */


    private JSONObject http_get(String url_string) throws Exception {
        /**
         * Performs an http get request to get all possible services from the specific solace cloud
         *
         * @param url_string is a URL in which the http get request will be performed on
         * @return a HashMap including the name of each service as well as it's service ID
         */

        URL url = new URL(url_string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty("Content-Type", "application/json");

        // INSERT YOUR API TOKEN BELOW
        connection.setRequestProperty("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJFUzUxMiJ9.eyJzdWIiOiIyNDRnN25jMGdzMDkiLCJpbm5lclRva2VuIjoiZXlKMGVYQWlPaUpLVjFRaUxDSmhiR2NpT2lKRlV6VXhNaUo5LmV5SnZjbWNpT2lJeU5EUm5OMjVqTUdkeWVuQWlMQ0pqY21WaGRHVmtJam94TlRjM01USTJNRE13TmpVNUxDSmxiV0ZwYkNJNkltUnBZVzV1WVM1dFkyRnNiR2x6ZEdWeVFHMWhhV3d1ZFhSdmNtOXVkRzh1WTJFaUxDSndaWEp0YVhOemFXOXVjeUk2V3lKcFlXMWZkWE5sY25NNloyVjBJaXdpYzJWeWRtbGpaWE02WjJWME9uTmxiR1lpTENKelpYSjJhV05sYzE5eVpYRjFaWE4wY3pwblpYUWlMQ0p6WlhKMmFXTmxYM0psY1hWbGMzUnpPbkJ2YzNRNlkyeHBaVzUwWDNCeWIyWnBiR1VpTENKcFlXMWZiM0puWDNKdmJHVnpPbWRsZENJc0luTmxjblpwWTJWek9uQnZjM1FpTENKelpYSjJhV05sY3pwa1pXeGxkR1U2YzJWc1ppSXNJbk5sY25acFkyVnpPbWRsZENKZGZRLmFFMDVtdWNoSVYtWVBwTWtEWjUxbXBHM05uUjFmRXZKNXBsM1FFMUVQV2siLCJhcGlUb2tlbklkIjoiMjVzNmRmcmJrenFkIiwiaWF0IjoxNTc3MTI2MDMwfQ.aKh-M3JMGj1wSH3QeWAzdwxqQOVQPzNLPX89xz48hBk");

        StringBuilder content;

        // Get the input stream of the connection
        try (BufferedReader input = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            String line;
            content = new StringBuilder();
            while ((line = input.readLine()) != null) {
                // Append each line of the response and separate them
                content.append(line);
                content.append(System.lineSeparator());
            }
        } finally {
            // Disconnect the connection
            connection.disconnect();
        }

        Object obj = new JSONParser().parse(content.toString());
        // Typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        return jo;

    }

    public HashMap<String, String> get_all_services() throws Exception {
        /**
         * Searches the JSON object returned from a http get request to find every service and it's service ID
         *
         * @return a HashMap including the name of each service as well as it's service ID
         */

        JSONObject jo = http_get("https://api.solace.cloud/api/v0/services");
        if (jo == null) { // The get request did not work properly
            return new HashMap<>();
        }

        HashMap<String, String> services = new HashMap<>();

        // Find the correct location in the JSON object that contains each service name and it's service ID
        JSONArray arr_json = (JSONArray) jo.get("data");
        for (int i=0; i<arr_json.size(); i++) {
            JSONObject service_data = (JSONObject) arr_json.get(i);
            if (service_data.get("type").equals("service")) {
                // Append the correct service name and service ID to the returning HashMap
                services.put((String) service_data.get("name"), (String) service_data.get("serviceId"));
            }
        }
        return services;
    }

    private JSONObject find_in_array(String key, String value, JSONArray array) {
        /**
         * Searches the JSON object returned from a http get request to find every service and it's service ID
         * @param key is the key that should be looked for in the specified array
         * @param value is the value of the specified key that should be looked for in the specified array
         * @param array is the array that is searched for a specific key and value
         *
         * @return the JSON Object that matches the given key and value in the given array
         */

        JSONObject result = null;

        for (int i = 0; i < array.size(); i++) {
            result = (JSONObject) array.get(i);
            if (result.get(key).equals(value)) {
                break;
            }
        }

        return result;
    }

    public ConnectionData get_service_by_ID(String serviceID) throws Exception {
        /**
         * Create a ConnectionData object by parsing the JSON Object returned from an http get request and finding
         * the host, username, password and vpn name of the specific service
         *
         * @param serviceID indicates which service should be searched to receive all the necessary information to
         *                  create the ConnectionData object
         * @return a ConnectionData object with the host, username, password and vpn name needed for a subscriber and
         * consumer to connect to a broker
         */

        StringBuilder get_service_URL = new StringBuilder("https://api.solace.cloud/api/v0/services/");
        get_service_URL.append(serviceID); // Build the URL for the specific service

        JSONObject jo = http_get(get_service_URL.toString()); // Perform the http get request on the specific service
        if (jo == null) {
            return null;
        }

        // Parse through the JSON Object to get to the correct level at which the username and password for the
        //  service could be found
        JSONObject service_data = (JSONObject) jo.get("data");
        JSONArray service_protocols = (JSONArray) service_data.get("messagingProtocols");
        JSONObject protocol_data = find_in_array("name", "SMF", service_protocols);
        if (protocol_data == null) {
            return null;
        }

        String service_username = (String) protocol_data.get("username"); // Find username for the specific service
        String service_password = (String) protocol_data.get("password"); // Find password for the specific service


        // Find host for the specific service
        JSONObject protocol_uri = find_in_array("name", "SMF", (JSONArray) protocol_data.get("endPoints"));
        if (protocol_uri == null) {
            return null;
        }
        String service_uri = (String) ((JSONArray) protocol_uri.get("uris")).get(0);

        // Find vpn name for the specific service
        String vpn_name = (String) ((JSONObject)service_data.get("msgVpnAttributes")).get("vpnName");

        // Create a Connection Data instance
        ConnectionData connectionData = new ConnectionData(service_uri, vpn_name, service_username, service_password);

        return connectionData;
    }

}
