package Server.Middlewares;

import java.util.HashMap;

public class MiddlewareRegister {

    HashMap<String, Middleware> middlewareList;
    public MiddlewareRegister() {
        this.middlewareList = new HashMap<String, Middleware>();
    }

    public void register(String middlewareName, Middleware middleware) {
        // Register the middleware

        this.middlewareList.put(middlewareName, middleware);

    }

    public Middleware get(String middlewareName) {
        return middlewareList.get(middlewareName);
    }
}
