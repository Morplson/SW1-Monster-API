package Server.Middlewares;

import Server.Models.User;

import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SessionManager implements Middleware{

    HashMap<String, User> sessionData;



    HashMap<String, String> accessData;
    HashMap<String, Long> timestamp;

    //long timeout = 0;

    public SessionManager() {
        this.sessionData = new HashMap<String, User>();
        this.accessData = new HashMap<String, String>();
        this.timestamp = new HashMap<String, Long>();

        this.accessData.put("Basic admin-mtcgToken", "\\S+");
    }

    public boolean register(String token, String domain) {
        if (!this.accessData.containsKey(token)) {
            this.accessData.put(token, domain);
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

    public boolean hasAccess(String token, String domainName) {
        //if (!this.isNotTimedout(token)) {
        //    return false;
        //}

        if (!this.accessData.containsKey(token)) {
            return false;
        }

        String value = this.accessData.get(token);

        Pattern p = Pattern.compile(value);

        // Ein Matcher-Objekt wird erstellt, um den Pfad der Anfrage mit dem Schlüssel zu vergleichen.
        Matcher matcher = p.matcher(domainName);

        if (!matcher.matches()) {
            // set the worker if matches :)
            return false;
        }

        return true;
    }

    public HashMap<String, String> getAccessData() {
        return accessData;
    }

    public void setAccessData(HashMap<String, String> accessData) {
        this.accessData = accessData;
    }


}
