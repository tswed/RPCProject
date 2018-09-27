import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface ICarServer extends Remote {
    HashMap<String, Car> GetCars() throws RemoteException;
    HashMap<String, Reservation> GetCarReservations() throws RemoteException;
    String AddCarReservation(String name, String carModel, String startDate, String endDate) throws RemoteException;
    String CancelCarReservation(String name) throws RemoteException;
}
