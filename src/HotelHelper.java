import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DriverManager;

public class HotelHelper {
    public void initializeRegistry() throws Exception {
        System.setProperty("java.rmi.server.hostname", "172.22.181.50");
        HotelServer obj = new HotelServer();
        IHotelServer stub = (IHotelServer) UnicastRemoteObject.exportObject(obj, 0);

        LocateRegistry.createRegistry(5003);
        Registry registry = LocateRegistry.getRegistry("172.22.181.50",5003);

        registry.bind("Hotel", stub);

    }

    public Connection initializeDBConnection() throws Exception {
        String url = "jdbc:mysql://172.22.181.42:3306/rpc_project";
        String username = "thomas2";
        String password = "password";

        Connection connection = DriverManager.getConnection(url, username, password);
        System.out.println("Database connected!");
        return connection;
    }
}
