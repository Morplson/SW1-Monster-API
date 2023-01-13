package Server.RouteWorkers;

import Server.HTTPUtil.HTTPPackage;
import Server.Middlewares.MiddlewareRegister;

public interface RouteWorker {
    HTTPPackage process(HTTPPackage request, MiddlewareRegister mr);
}
