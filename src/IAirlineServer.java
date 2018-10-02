import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface IAirlineServer extends Remote {
    HashMap<String, Airline> GetAirlines() throws RemoteException;
    HashMap<String, Reservation> GetAirlineReservations() throws RemoteException;
    String AddAirlineReservation(String name, String nameOfAirline, String startDate, String endDate) throws RemoteException;
    String CancelAirlineReservation(String name) throws RemoteException;
    void CloseConnection() throws RemoteException;
}
