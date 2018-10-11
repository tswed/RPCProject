import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;

public class AirlineHelper {

    public AirlineHelper() {
    }

    public void initializeRegistry() throws Exception {
        System.setProperty("java.rmi.server.hostname", "192.168.56.1");
        AirlineServer obj = new AirlineServer();
        IAirlineServer stub = (IAirlineServer) UnicastRemoteObject.exportObject(obj, 0);

        LocateRegistry.createRegistry(5002);
        Registry registry = LocateRegistry.getRegistry("192.168.56.1",5002);

        registry.bind("Airline", stub);

    }

    public Connection initializeDBConnection() throws Exception {
        String url = "jdbc:mysql://172.29.245.103:3306/rpc_project";
        String username = "Thomas.Swed";
        String password = "password";

        Connection connection = DriverManager.getConnection(url, username, password);
        System.out.println("Database connected!");
        return connection;
    }
}
