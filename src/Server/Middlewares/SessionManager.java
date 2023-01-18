package Server.Middlewares;

import Server.Models.User;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionManager implements Middleware{
    HashMap<String, AccessData> accessData;

    public SessionManager() {
        this.accessData = new HashMap<String, AccessData>();
    }

    public boolean register(String token, String domain, String username) {
        if (!this.accessData.containsKey(token)) {
            AccessData ad = new AccessData(domain, username);
            this.accessData.put(token, ad);
            return true;
        }

        return false;
    }

    public boolean logout(String token) {
        if (this.accessData.containsKey(token)) {
            this.accessData.remove(token);
            return true;
        }

        return false;
    }

    public boolean isLoggedIn(String token) {
        if (this.accessData.containsKey(token)) {
            return true;
        }

        return false;
    }

    public boolean hasAccess(String token, String domainName) {

        if (!this.accessData.containsKey(token)) {
            return false;
        }

        AccessData ad = this.accessData.get(token);

        Pattern p = Pattern.compile(ad.getDomain());

        // Ein Matcher-Objekt wird erstellt, um den Pfad der Anfrage mit dem Schlüssel zu vergleichen.
        Matcher matcher = p.matcher(domainName);

        if (!matcher.matches()) {
            // set the worker if matches :)
            return false;
        }

        return true;
    }

    public String getUsername(String token) {
        AccessData ad = accessData.get(token);
        return ad!=null?ad.getUsername():null;
    }

    public HashMap<String, AccessData> getAccessData() {
        return this.accessData;
    }
}


