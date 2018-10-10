import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class CarServer implements ICarServer {
    private HashMap<String, Car> cars = new HashMap<>();
    private HashMap<String, Reservation> reservations = new HashMap<>();
    private static Connection connection;

    public CarServer() {
//        cars.put("Rav4", new Car("Toyota", 1, "Mid-Size", "Rav4", 2018, 3));
//        cars.put("4Runner", new Car("Toyota", 2, "Huge", "4Runner", 2016, 4));
//        cars.put("Taurus", new Car("Ford", 3, "Small", "Taurus", 2017, 15));
    }

    private static void initializeDBData() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO cars (id, make, model, size, year, qty_available)" +
                    "VALUES ('1', 'Toyota', 'Rav4', 'Mid', '2012', '4')," +
                    "('2', 'Toyota', '4Runner', 'Huge', '2017', '6')," +
                    "('3', 'Ford', 'Mustang', 'Small', '2019', '3')");
            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to initialize car data.");
            e.printStackTrace();
        }
    }
    public HashMap<String, Car> GetCars() {
        System.out.println("GetCars method called.");

        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("Select * from cars");

            while (result.next()) {
                Car car = new Car(result.getString("make"),
                        result.getInt("id"), result.getString("size"),
                        result.getString("model"), result.getInt("year"),
                        result.getInt("qty_available"));
                cars.put(result.getString("model"), car);
            }

            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to return car data");
            e.printStackTrace();
        }

        return cars;
    }

    public HashMap<String, Reservation> GetCarReservations() {
        System.out.println("GetCarReservations function called.");

        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * from reservations WHERE reservation_type = 'Car'");

            while (result.next()) {
                Reservation reservation = new Reservation(result.getInt("id"), result.getString("reservation_type"),
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

    public String AddCarReservation(String guestName, String carModel, String startDate, String endDate) {
        System.out.println("AddCarReservations function called.");

        cars = GetCars();
        reservations.clear();
        reservations = GetCarReservations();

        if (reservations.containsKey(guestName)) {
            return "Reservation already added for guest: " + guestName;
        }

        Car carRequested = cars.get(carModel);

        if (carRequested != null) {
            try {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("INSERT INTO reservations (id, reservation_name,  reservation_type, guest_name, start_date, end_date)" +
                        "VALUES ('" + carRequested.getId() + "', '" + carRequested.getCarMake()
                        + "', '" + "Car" + "', '" + guestName + "', '" + startDate + "', '" + endDate + "');");

                int qtyAvailable = carRequested.getQtyAvailable() - 1;

                String sql = "UPDATE cars SET qty_available="+ qtyAvailable + " WHERE model='" + carModel + "';";

                stmt.executeUpdate(sql);

                stmt.close();
            } catch (Exception e) {
                System.out.println("Failed to add car reservation.");
                e.printStackTrace();
            }

            reservations.put(guestName, new Reservation(carRequested.getId(),"Car", guestName, carModel, startDate, endDate));
        } else {
            return "Car not found or not available.  Please try a different car.";
        }

        return "You added a car reservation for guest: " + guestName + ", car: " + carRequested.getModel();
    }

    public String CancelCarReservation(String name) {
        System.out.println("CancelCarReservation function called.");

        reservations = GetCarReservations();
        cars = GetCars();

        if (reservations.containsKey(name)) {
            return "Reservation not found for guest: " + name;
        }

        Reservation carReservation = reservations.get(name);

        if (carReservation != null) {
            try {
                Car car = cars.get(carReservation.getNameOfReservation());

                Statement stmt = connection.createStatement();
                stmt.executeUpdate("DELETE FROM reservations" +
                        "WHERE guest_name = " + "'" + name + "'" + " and reservation_type = " + "'Car'");

                int qty_available = car.getQtyAvailable() + 1;

                stmt.executeUpdate("UPDATE cars" +
                        "SET qty_available="+ qty_available + " WHERE model='" + car.getModel() +"';");

                stmt.close();
            } catch (Exception e) {
                System.out.println("Failed to cancel car reservation.");
                e.printStackTrace();
            }

        } else {
            return "Reservation not found for guest: " + name +".  Please try a different reservation.";
        }

        return "You canceled a car reservation for: " + name;
    }

    public static void main(String args[]) {
        CarHelper carHelper = new CarHelper();
        try {
            carHelper.initializeRegistry();
            connection = carHelper.initializeDBConnection();
            initializeDBData();

            System.err.println("CarServer waiting for client to connect...");
        } catch (SQLException e) {
            System.out.println("Cannot connect the database!");
        } catch (Exception e) {
            System.err.println("CarServer exception: " + e.toString());
        }
    }
}