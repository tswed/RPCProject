import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IHelloWorld extends Remote {
    String display(String name) throws RemoteException;
}

