public class ConnectionData {

    private String host;
    private String vpnName;
    private String username;
    private String password;

    ConnectionData(String host, String vpnName, String username, String password) {
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
