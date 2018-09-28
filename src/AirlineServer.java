import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class AirlineServer implements IAirlineServer {
    private HashMap<String, Airline> airlines = new HashMap<>();
    private HashMap<String, Reservation> reservations = new HashMap<>();

    public AirlineServer() {
        airlines.put("United", new Airline("United", 101, 55, 300.00));
        airlines.put("Delta", new Airline("Delta", 202, 34, 250.00));
        airlines.put("American", new Airline("American", 303, 72, 125.00));
    }

    public HashMap<String, Airline> GetAirlines() {
        System.out.println("GetAirlines function called.");
        return airlines;
    }

    public HashMap<String, Reservation> GetAirlineReservations() {
        System.out.println("GetAirlineReservations function called.");
        return reservations;
    }

    public String AddAirlineReservation(String name, String nameOfAirline, String startDate, String endDate) {
        System.out.println("AddAirlineReservations function called.");

        Airline airlineRequested = airlines.get(nameOfAirline);
        if (airlineRequested != null) {
            airlineRequested.setSeatsAvailable(airlineRequested.getSeatsAvailable() - 1);

            reservations.put(name, new Reservation("Airline", name, nameOfAirline, startDate, endDate));
        } else {
            return "Airline not found or not available.  Please try a different airline.";
        }

        try {
            Thread.sleep(200000);
        } catch (Exception e) {
            System.out.println("Thread.sleep didn't work");
        }

        return "You reserved an Airline for guest: " + name + ", on airline: " + nameOfAirline;
    }

    public String CancelAirlineReservation(String name) {
        System.out.println("CancelAirlineReservations function called.");

        Reservation airlineReservation = reservations.get(name);

        if (airlineReservation != null) {
            Airline airline = airlines.get(airlineReservation.getNameOfReservation());
            airline.setSeatsAvailable(airline.getSeatsAvailable() + 1);

            reservations.remove(name);
        } else {
            return "Reservation not found.  Please try a different reservation.";
        }

        return "You canceled an airline reservation for guest: " + name;
    }

    public static void main(String args[]) {
        try {
            System.setProperty("java.rmi.server.hostname", "172.22.181.41");
            AirlineServer obj = new AirlineServer();
            IAirlineServer stub = (IAirlineServer) UnicastRemoteObject.exportObject(obj, 0);

            LocateRegistry.createRegistry(5002);
            Registry registry = LocateRegistry.getRegistry("172.22.181.41",5002);

            registry.bind("Airline", stub);

            System.err.println("AirlineServer waiting for client to connect...");
        } catch (Exception e) {
            System.err.println("AirlineServer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
