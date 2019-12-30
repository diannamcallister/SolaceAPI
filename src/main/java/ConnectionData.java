public class ConnectionData {
    /**
     * A ConnectionData class that holds all the necessary information of a broker for a publisher or subscriber to
     * connect to the specific broker
     */

    private String host;
    private String vpnName;
    private String username;
    private String password;

    ConnectionData(String host, String vpnName, String username, String password) {
        /**
         * Constructor that creates an instance of the ConnectionData class that contains necessary information for
         *  a producer and consumer to connect to a broker
         *
         * @param host is the name of the host for the specific service
         * @param vpnName is the vpn name of the specific service
         * @param username of the specific service
         * @param password of the specific service
         *
         */
        this.host = host;
        this.vpnName = vpnName;
        this.username = username;
        this.password = password;
    }

    public String getHost() {
        return this.host;
    }

    public String getVpnName() {
        return this.vpnName;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }
}
