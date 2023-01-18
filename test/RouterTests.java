import Server.HTTPUtil.HTTPMethod;
import Server.HTTPUtil.HTTPPackage;
import Server.HTTPUtil.HTTPParser;
import Server.Middlewares.MiddlewareRegister;
import Server.RouteWorkers.RouteWorker;
import Server.Routing.RoutingTable;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class RouterTests {



    @Test
    public void testConstructor() {
        RoutingTable table = new RoutingTable(new MiddlewareRegister());
        assertNotNull(table.getUriList().get(HTTPMethod.GET));
        assertNotNull(table.getUriList().get(HTTPMethod.POST));
        assertNotNull(table.getUriList().get(HTTPMethod.DELETE));
        assertNotNull(table.getUriList().get(HTTPMethod.PUT));
    }


    @Test
    public void testAdd() {
        RoutingTable table = new RoutingTable(new MiddlewareRegister());
        RouteWorker testWorker = new RouteWorker() {
            @Override
            public HTTPPackage process(HTTPPackage httpPackage, MiddlewareRegister mr) {
                return null;
            }
        };
        table.add(HTTPMethod.GET, "\\/test", testWorker);
        assertEquals(testWorker, table.getUriList().get(HTTPMethod.GET).get("\\/test"));
    }

    @Test
    public void testAddGet() {
        RoutingTable table = new RoutingTable(new MiddlewareRegister());
        RouteWorker testWorker = new RouteWorker() {
            @Override
            public HTTPPackage process(HTTPPackage httpPackage, MiddlewareRegister mr) {
                return null;
            }
        };
        table.addGet("\\/test", testWorker);
        assertEquals(testWorker, table.getUriList().get(HTTPMethod.GET).get("\\/test"));
    }

    @Test
    public void testAddDelete() {
        RoutingTable table = new RoutingTable(new MiddlewareRegister());
        RouteWorker testWorker = new RouteWorker() {
            @Override
            public HTTPPackage process(HTTPPackage httpPackage, MiddlewareRegister mr) {
                return null;
            }
        };
        table.addDelete("\\/test", testWorker);
        assertEquals(testWorker, table.getUriList().get(HTTPMethod.DELETE).get("\\/test"));
    }

    @Test
    public void testMatchBasic() {
        RoutingTable table = new RoutingTable(new MiddlewareRegister());
        RouteWorker testWorker = new RouteWorker() {
            @Override
            public HTTPPackage process(HTTPPackage httpPackage, MiddlewareRegister mr) {
                return HTTPPackage.generateBasicResponse("success",true);
            }
        };
        table.addGet("\\/test", testWorker);

        HTTPPackage httpPackage = new HTTPPackage();
        httpPackage.setMethod(HTTPMethod.GET);
        httpPackage.setUri("/test");

        HTTPPackage result = table.process(httpPackage);
        assertEquals("success", result.getBodyPlain());
    }

    @Test
    public void testMatchIntermediate() throws IOException {
        RoutingTable table = new RoutingTable(new MiddlewareRegister());
        RouteWorker testWorker = new RouteWorker() {
            @Override
            public HTTPPackage process(HTTPPackage httpPackage, MiddlewareRegister mr) {
                return HTTPPackage.generateBasicResponse("success",true);
            }
        };
        table.addGet("\\/test", testWorker);

        String input =
                "GET /test HTTP/1.1Host: localhost:10001\n" +
                "User-Agent: curl/7.83.1\n" +
                "Accept: */*\n" +
                "Content-Type: application/json\n" +
                "Authorization: hehe\n" +
                "Content-Length: 0\n"
        ;

        InputStream stream = new ByteArrayInputStream(input.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        HTTPPackage httpPackage = HTTPParser.parse(reader);

        HTTPPackage result = table.process(httpPackage);
        assertEquals("success", result.getBodyPlain());
    }

    @Test
    public void testMatchAdvanced() throws IOException {
        RoutingTable table = new RoutingTable(new MiddlewareRegister());
        RouteWorker testWorker = new RouteWorker() {
            @Override
            public HTTPPackage process(HTTPPackage httpPackage, MiddlewareRegister mr) {
                String requestURI = httpPackage.getUri();
                String id = requestURI.split("/")[2];

                return HTTPPackage.generateBasicResponse(id,true);
            }
        };
        table.addGet("\\/test\\/\\S+", testWorker);

        String id = "happy";

        String input =
                "GET /test/"+id+" HTTP/1.1Host: localhost:10001\n" +
                        "User-Agent: curl/7.83.1\n" +
                        "Accept: */*\n" +
                        "Content-Type: application/json\n" +
                        "Authorization: hehe\n" +
                        "Content-Length: 0\n"
                ;

        InputStream stream = new ByteArrayInputStream(input.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

        HTTPPackage httpPackage = HTTPParser.parse(reader);

        HTTPPackage result = table.process(httpPackage);
        assertEquals(id, result.getBodyPlain());
    }






}
