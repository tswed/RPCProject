import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.HashMap;

public interface IHotelServer extends Remote {
    HashMap<String, Hotel> GetHotels() throws RemoteException;
    HashMap<String, Reservation> GetHotelReservations() throws RemoteException;
    String AddHotelReservation(String name, String nameOfHotel, String startDate, String endDate) throws RemoteException;
    String CancelHotelReservation(String name) throws RemoteException;

}
