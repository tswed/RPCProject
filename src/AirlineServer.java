import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;


public class AirlineServer implements IAirlineServer {
    private HashMap<String, Airline> airlines = new HashMap<>();
    private HashMap<String, Reservation> reservations = new HashMap<>();
    static Connection connection;

    public AirlineServer() {
//        airlines.put("United", new Airline("United", 101, 55, 300.00));
//        airlines.put("Delta", new Airline("Delta", 202, 34, 250.00));
//        airlines.put("American", new Airline("American", 303, 72, 125.00));
    }

    private static void initializeDBData() {
        try {
            Statement stmt = connection.createStatement();
            stmt.execute("INSERT INTO Airlines (flight_num, airline_name, seats_available, departure_city, destination_city)" +
                    "VALUES ('101', 'United', '20', 'Chicago', 'Austin')," +
                    "('202', 'Delta', '38', 'Chicago', 'Paris')," +
                    "('303', 'American', '22', 'Chicago', 'New York City)");
            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to initialize airline data");
        }

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
        AirlineHelper airlineHelper = new AirlineHelper();
        try {
            airlineHelper.iniitializeRegistry();
            connection = airlineHelper.initializeDBConnection();
            initializeDBData();

            System.err.println("AirlineServer waiting for client to connect...");
        } catch (SQLException e) {
            System.out.println("Cannot connect the database!");
        } catch (Exception e) {
            System.err.println("AirlineServer exception: " + e.toString());
        }
        finally {
            try {
                connection.close();
            } catch (Exception e) {
                System.out.println("Unable to close database connection.");
            }
        }
    }
}
