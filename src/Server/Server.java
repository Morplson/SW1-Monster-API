package Server;

import Server.HTTPUtil.HTTPPackage;
import Server.HTTPUtil.HTTPParser;
import Server.Middlewares.*;
import Server.RouteWorkers.BattleWorkers.BattleRouteWorker;
import Server.RouteWorkers.BattleWorkers.ShowScoreboardRouteWorker;
import Server.RouteWorkers.BattleWorkers.ShowUserStatsRouteWorker;
import Server.RouteWorkers.CardWorkers.*;
import Server.RouteWorkers.TradingWorkers.CreateTradingDealRouteWorker;
import Server.RouteWorkers.TradingWorkers.DeleteTradingDealRouteWorker;
import Server.RouteWorkers.TradingWorkers.ShowTradingDealRouteWorker;
import Server.RouteWorkers.TradingWorkers.TradeRouteWorker;
import Server.RouteWorkers.UserWorkers.*;
import Server.Routing.InvalidRouteException;
import Server.Routing.RoutingTable;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class Server implements Runnable{
    public static void main(String[] args) throws IOException {
        System.out.println("Starting Server.HTTPServer");
        System.out.println("localhost:10001");

        // server is listening on port 10001
        ServerSocket ss = new ServerSocket(10001);

        // Set up database connection
        Database db = new Database();

        // Set up sessionmanager
        SessionManager sm = new SessionManager();

        // Set up BattleQueue
        BattleQueue bq = new BattleQueue();

        // Set up middleweare
        MiddlewareRegister middlewareRegister = new MiddlewareRegister();

        // Register middlewares
        middlewareRegister.register("db", db); //register database as "db"
        middlewareRegister.register("sm", sm); //sessionmanager database as "sm"
        middlewareRegister.register("bq", bq); //battlequeue database as "bq"



        // Set up routing table
        RoutingTable routingTable = new RoutingTable(middlewareRegister);

        //Create User (Registration)
        routingTable.addPost("\\/users", new CreateUserRouteWorker());
        //Show user data
        routingTable.addGet("\\/users\\/\\S+", new ShowUserRouteWorker());
        //edit user data
        routingTable.addPut("\\/users\\/\\S+", new EditUserRouteWorker());

        //Login Users
        routingTable.addPost("\\/sessions", new LoginUserRouteWorker());
        //Show sessions
        routingTable.addGet("\\/sessions", new ShowSessionsRouteWorker());
        //Logout Users
        routingTable.addDelete("\\/sessions\\/\\S+", new LogoutUserRouteWorker());



        //create packages (done by "admin")
        routingTable.addPost("\\/packages", new CreatePackageRouteWorker());
         //acquire packages
        routingTable.addPost("\\/transactions\\/packages", new AcquirePackageRouteWorker());
         //show all cards
        routingTable.addGet("\\/cards", new ShowUserCardsRouteWorker());
        //show deck (uses get params)
        routingTable.addGet("\\/deck", new ShowUserDeckRouteWorker());
        //configure deck
        routingTable.addPut("\\/deck", new EditUserDeckRouteWorker());


        //battle
        routingTable.addPost("\\/battles", new BattleRouteWorker());
        //stats (??? own elo I think?)
        routingTable.addGet("\\/stats", new ShowUserStatsRouteWorker());
        //scoreboard
        routingTable.addGet("\\/score", new ShowScoreboardRouteWorker());




        //check trading deals
        routingTable.addGet("\\/tradings", new ShowTradingDealRouteWorker());
        //create trading deal
        routingTable.addPost("\\/tradings", new CreateTradingDealRouteWorker());
        //delete trading deal
        routingTable.addDelete("\\/tradings\\/\\S+", new DeleteTradingDealRouteWorker());
        //do a trade
        routingTable.addPost("\\/tradings\\/\\S+", new TradeRouteWorker());

        /**

         */

        ExecutorService pool = Executors.newFixedThreadPool(9);



        // running infinite loop for getting
        // client request
        while (true)
        {

            Socket s = null;


            try {
                // get client connection
                s = ss.accept();

                // socket object to receive incoming client requests
                System.out.println("A new client is connected : " + s);

                System.out.println("Assigning new thread for this client");

                // create a new thread object
                pool.execute(new Server(s, routingTable));
            }
            catch (Exception e){
                s.close();
                e.printStackTrace();
            }
        }
    }

    BufferedReader buffread;
    PrintWriter printer;
    Socket s;
    RoutingTable routingTable;

    public Server(Socket s, RoutingTable routingTable) {
        this.s = s;
        this.routingTable = routingTable;
    }


    @Override
    public void run() {

        HTTPPackage httpPackage = new HTTPPackage();

        try {
            // get client input stream
            InputStream is = this.s.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            this.buffread = new BufferedReader(inputStreamReader);

            // get client output stream
            OutputStream os = s.getOutputStream();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(os);
            this.printer = new PrintWriter(outputStreamWriter);

            httpPackage = HTTPParser.parse(buffread);
        } catch ( IOException ioexception) {
            ioexception.printStackTrace();
        } catch ( Exception e) {
            e.printStackTrace();
        }

        System.out.println("Raw massage: \n" + httpPackage.getRaw());

        String res;
        HTTPPackage resPackage;
        try {
            resPackage = this.routingTable.process(httpPackage);
        } catch (InvalidRouteException e){
            resPackage = HTTPPackage.generateErrorResponse(404, "Not Found", "Route Not Found");
        } catch (Exception e) {
            e.printStackTrace();
            resPackage = HTTPPackage.generateErrorResponse(500, "Server Error", e.getMessage());
        }

        res = HTTPParser.build(resPackage);

        if(res.isEmpty()){
            printer.write("HTTP/1.1 500 Parsing error\r\nContent-Type: text/plain;charset=utf-8\r\nContent-Length: 19\r\nConnection: close\r\n\r\nError while parsing");
        } else {
            System.out.println("Raw response: \n" + res);
            printer.write(res);
        }


        try {
            printer.close();
            buffread.close();
        } catch (Exception e){}


    }

    //}



}

