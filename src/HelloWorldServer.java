import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class HelloWorldServer implements IHelloWorld {

    public HelloWorldServer() {}

    public String display(String name) {
        return "Hello, " + name;
    }

    public static void main(String args[]) {
        try {
            HelloWorldServer obj = new HelloWorldServer();
            IHelloWorld stub = (IHelloWorld) UnicastRemoteObject.exportObject(obj, 0);

            LocateRegistry.createRegistry(5000);
            Registry registry = LocateRegistry.getRegistry("192.168.1.66",5000);

            registry.bind("HelloWorld", stub);

            System.err.println("HelloWorldServer waiting for client to connect...");
        } catch (Exception e) {
            System.err.println("HelloWorldServer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
