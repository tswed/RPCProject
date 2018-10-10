import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class HotelServer implements IHotelServer {
    private HashMap<String, Hotel> hotels = new HashMap<>();
    private HashMap<String, Reservation> reservations = new HashMap<>();
    private static Connection connection;

    public HotelServer() {
        hotels.put("Marriott", new Hotel(77,"Marriott", 40, 125.00));
        hotels.put("Best Western", new Hotel(88,"Best Western", 8, 80.00));
        hotels.put("Shady Motel", new Hotel(99,"Shady Motel", 50, 39.00));
    }

    private static void initializeDBData() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO hotels (id, name, num_rooms_available, price)" +
                    "VALUES ('77', 'Marriott', '40', '125.00')," +
                    "('88', 'Best Western', '8', '80.00')," +
                    "('99', 'Shady Motel', '50', '39.00')");
            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to initialize hotel data.");
            e.printStackTrace();
        }
    }

    public HashMap<String, Hotel> GetHotels() {
        System.out.println("GetHotels function called.");

        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("Select * from hotels");

            while (result.next()) {
                Hotel hotel = new Hotel(result.getInt("id"), result.getString("name"),
                        result.getInt("num_rooms_available"), result.getDouble("price"));
                hotels.put(result.getString("name"), hotel);
            }

            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to return hotel data");
            e.printStackTrace();
        }

        return hotels;
    }

    public HashMap<String, Reservation> GetHotelReservations() {
        System.out.println("GetHotelReservations function called.");

        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * from reservations WHERE reservation_type = 'Hotel'");

            while (result.next()) {
                Reservation reservation = new Reservation(result.getInt("id"),result.getString("reservation_type"),
                        result.getString("guest_name"), result.getString("reservation_name"),
                        result.getString("start_date"), result.getString("end_date"));
                reservations.put(result.getString("guest_name"), reservation);
            }

            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to return reservation data.");
            e.printStackTrace();
        }

        return reservations;
    }

    public String AddHotelReservation(String guestName, String nameOfHotel, String startDate, String endDate) {
        System.out.println("AddHotelReservation function called.");

        hotels = GetHotels();
        reservations.clear();
        reservations = GetHotelReservations();

        if (reservations.containsKey(guestName)) {
            return "Reservation already added for guest: " + guestName;
        }

        Hotel hotelRequested = hotels.get(nameOfHotel);

        if (hotelRequested != null) {
            try {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("INSERT INTO reservations (id, reservation_name,  reservation_type, guest_name, start_date, end_date)" +
                        "VALUES ('" + hotelRequested.getId() + "', '" + hotelRequested.getHotelName()
                        + "', '" + "Hotel" + "', '" + guestName + "', '" + startDate + "', '" + endDate + "');");

                int numRoomsAvailable = hotelRequested.getNumOfRoomsAvailable() - 1;

                String sql = "UPDATE hotels SET num_rooms_available="+ numRoomsAvailable + " WHERE name='" + hotelRequested.getHotelName() + "';";

                stmt.executeUpdate(sql);

                stmt.close();
            } catch (Exception e) {
                System.out.println("Failed to add reservation.");
                e.printStackTrace();
            }

            reservations.put(guestName, new Reservation(hotelRequested.getId(),"Airline", guestName, nameOfHotel, startDate, endDate));
        } else {
            return "Hotel not found or not available.  Please try a different hotel.";
        }

        return "Hotel reservation added for guest: " + guestName + ", hotel: " + nameOfHotel;
    }

    public String CancelHotelReservation(String name) {
        System.out.println("CancelHotelReservation function called.");

        reservations = GetHotelReservations();
        hotels = GetHotels();

        if (reservations.containsKey(name)) {
            return "Reservation not found for guest: " + name;
        }

        Reservation hotelReservation = reservations.get(name);

        if (hotelReservation != null) {
            try {
                Hotel hotelRequested = hotels.get(hotelReservation.getNameOfReservation());

                Statement stmt = connection.createStatement();
                stmt.executeUpdate("DELETE FROM reservations" +
                        "WHERE guest_name = " + "'" + name + "'" + " and reservation_type = " + "'Hotel'");

                int numRoomsAvailable = hotelRequested.getNumOfRoomsAvailable() + 1;

                stmt.executeUpdate("UPDATE hotels" +
                        "SET num_rooms_available="+ numRoomsAvailable + " WHERE name='" + hotelRequested.getHotelName() +"';");

                stmt.close();
            } catch (Exception e) {
                System.out.println("Failed to cancel reservation.");
                e.printStackTrace();
            }

        } else {
            return "Reservation not found.  Please try a different reservation.";
        }

        return "You canceled a hotel reservation for guest: " + name;
    }

    public static void main(String args[]) {
        HotelHelper hotelHelper = new HotelHelper();
        try {
            hotelHelper.initializeRegistry();
            connection = hotelHelper.initializeDBConnection();
            initializeDBData();

            System.err.println("HotelServer waiting for client to connect...");
        } catch (SQLException e) {
            System.out.println("Cannot connect the database!");
        } catch (Exception e) {
            System.err.println("HotelServer exception: " + e.toString());
        }
    }
}