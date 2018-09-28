import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class HotelServer implements IHotelServer {
    private HashMap<String, Hotel> hotels = new HashMap<>();
    private HashMap<String, Reservation> reservations = new HashMap<>();

    public HotelServer() {
        hotels.put("Marriott", new Hotel("Marriott", 4, 125.00));
        hotels.put("Best Western", new Hotel("Best Western", 8, 80.00));
        hotels.put("Shady Motel", new Hotel("Shady Motel", 50, 39.00));
    }

    public HashMap<String, Hotel> GetHotels() {
        System.out.println("GetHotels function called.");
        return hotels;
    }

    public HashMap<String, Reservation> GetHotelReservations() {
        System.out.println("GetHotelReservations function called.");
        return reservations;
    }

    public String AddHotelReservation(String name, String nameOfHotel, String startDate, String endDate) {
        System.out.println("AddHotelReservation function called.");

        Hotel hotelRequested = hotels.get(nameOfHotel);
        if (hotelRequested != null) {
            hotelRequested.setNumOfRoomsAvailable(hotelRequested.getNumOfRoomsAvailable() - 1);

            reservations.put(name, new Reservation("Hotel", name, nameOfHotel, startDate, endDate));
        } else {
            return "Hotel not found or not available.  Please try a different hotel.";
        }

        return "Hotel reservation added for guest: " + name + ", hotel: " + nameOfHotel;
    }

    public String CancelHotelReservation(String name) {
        System.out.println("CancelHotelReservation function called.");

        Reservation hotelReservationRequested = reservations.get(name);

        if (hotelReservationRequested != null) {
            Hotel hotelOnReservation = hotels.get(hotelReservationRequested.getNameOfReservation());
            hotelOnReservation.setNumOfRoomsAvailable(hotelOnReservation.getNumOfRoomsAvailable() + 1);

            reservations.remove(name);
        } else {
            return "Reservation not found.  Please try a different reservation.";
        }

        return "You canceled a hotel reservation for guest: " + name;
    }

    public static void main(String args[]) {
        try {
            System.setProperty("java.rmi.server.hostname", "172.22.181.34");
            HotelServer obj = new HotelServer();
            IHotelServer stub = (IHotelServer) UnicastRemoteObject.exportObject(obj, 0);

            LocateRegistry.createRegistry(5001);
            Registry registry = LocateRegistry.getRegistry("172.22.181.34",5001);

            registry.bind("Hotel", stub);

            System.err.println("HotelServer waiting for client to connect...");
        } catch (Exception e) {
            System.err.println("HotelServer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}