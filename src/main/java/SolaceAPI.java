import com.oracle.javafx.jmx.json.JSONException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

//import jdk.nashorn.internal.parser.JSONParser;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.*;

public class SolaceAPI {


    private JSONObject http_get(String url_string) throws Exception {

        URL url = new URL(url_string);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        connection.setRequestMethod("GET");

        connection.setRequestProperty("Content-Type", "application/json");
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
            connection.disconnect();
        }

        Object obj = new JSONParser().parse(content.toString());
        // typecasting obj to JSONObject
        JSONObject jo = (JSONObject) obj;
        return jo;

    }

    public HashMap<String, String> get_all_services() throws Exception {
        JSONObject jo = http_get("https://api.solace.cloud/api/v0/services");
        if (jo == null) {
            return new HashMap<>();
        }

        HashMap<String, String> services = new HashMap<>();
        JSONArray arr_json = (JSONArray) jo.get("data");
        for (int i=0; i<arr_json.size(); i++) {
            JSONObject service_data = (JSONObject) arr_json.get(i);
            if (service_data.get("type").equals("service")) {
                services.put((String) service_data.get("name"), (String) service_data.get("serviceId"));
            }
        }
        return services;
    }

    private JSONObject find_in_array(String key, String value, JSONArray array) {

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
        StringBuilder get_service_URL = new StringBuilder("https://api.solace.cloud/api/v0/services/");
        get_service_URL.append(serviceID);

        JSONObject jo = http_get(get_service_URL.toString());
        if (jo == null) {
            return null;
        }

        JSONObject service_data = (JSONObject) jo.get("data");
        JSONArray service_protocols = (JSONArray) service_data.get("messagingProtocols");

        JSONObject protocol_data = find_in_array("name", "SMF", service_protocols);

        if (protocol_data == null) {
            return null;
        }

        String service_username = (String) protocol_data.get("username");
        String service_password = (String) protocol_data.get("password");


        JSONObject protocol_uri = find_in_array("name", "SMF", (JSONArray) protocol_data.get("endPoints"));

        if (protocol_uri == null) {
            return null;
        }

        String service_uri = (String) ((JSONArray) protocol_uri.get("uris")).get(0);

        String vpn_name = (String) ((JSONObject)service_data.get("msgVpnAttributes")).get("vpnName");

        ConnectionData connectionData = new ConnectionData(service_uri, vpn_name, service_username, service_password);

        System.out.println(connectionData);

        return connectionData;
    }

}
