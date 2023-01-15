package Server.Middlewares;

public class
AccessData {
    String domain;
    String username;

    public AccessData(String domain, String username) {
        this.domain = domain;
        this.username = username;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
