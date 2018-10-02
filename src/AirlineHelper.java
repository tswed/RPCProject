import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;

public class AirlineHelper {

    public AirlineHelper() {
    }

    public void iniitializeRegistry() throws Exception {
        System.setProperty("java.rmi.server.hostname", "172.22.181.38");
        AirlineServer obj = new AirlineServer();
        IAirlineServer stub = (IAirlineServer) UnicastRemoteObject.exportObject(obj, 0);

        LocateRegistry.createRegistry(5002);
        Registry registry = LocateRegistry.getRegistry("172.22.181.38",5002);

        registry.bind("Airline", stub);

    }

    public Connection initializeDBConnection() throws Exception {
        String url = "jdbc:mysql://localhost:3306/rpc_project";
        String username = "root";
        String password = "password";

        Connection connection = DriverManager.getConnection(url, username, password);
        System.out.println("Database connected!");
        return connection;
    }
}
