import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class AirlineServer implements IAirlineServer {
    private HashMap<String, Airline> airlines = new HashMap<>();
    private HashMap<String, Reservation> reservations = new HashMap<>();
    private static Connection connection;
    Producer<String, String> producer;

    public AirlineServer() {
        producer = createProducer();
    }

    private static void initializeDBData() {
        try {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("INSERT INTO airlines (flight_num, airline_name, seats_available, departure_city, destination_city)" +
                    "VALUES ('101', 'United', '20', 'Chicago', 'Austin')," +
                    "('202', 'Delta', '38', 'Chicago', 'Paris')," +
                    "('303', 'American', '22', 'Chicago', 'New York City')");
            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to initialize airline data.");
            e.printStackTrace();
        }
    }

    public HashMap<String, Airline> GetAirlines() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("Select * from airlines");

            while (result.next()) {
                Airline airline = new Airline(result.getString("airline_name"),
                        result.getInt("flight_num"), result.getInt("seats_available"),
                        result.getString("departure_city"), result.getString("destination_city"));
                airlines.put(result.getString("airline_name"), airline);
            }

            stmt.close();
        } catch (Exception e) {
            System.out.println("Failed to return airline data");
            e.printStackTrace();
        }

        return airlines;
    }

    public HashMap<String, Reservation> GetAirlineReservations() {
        try {
            Statement stmt = connection.createStatement();
            ResultSet result = stmt.executeQuery("SELECT * from reservations WHERE reservation_type = 'Airline'");

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

    public String AddAirlineReservation(String guestName, String nameOfAirline, String startDate, String endDate) {
        System.out.println("AddAirlineReservations function called.");

        airlines = GetAirlines();
        reservations.clear();
        reservations = GetAirlineReservations();

        if (reservations.containsKey(guestName)) {
            return "Reservation already added for guest: " + guestName;
        }

        Airline airlineRequested = airlines.get(nameOfAirline);

        if (airlineRequested != null) {
            try {
                Statement stmt = connection.createStatement();
                stmt.executeUpdate("INSERT INTO reservations (id, reservation_name,  reservation_type, guest_name, start_date, end_date)" +
                        "VALUES ('" + airlineRequested.getFlightNum() + "', '" + airlineRequested.getName()
                        + "', '" + "Airline" + "', '" + guestName + "', '" + startDate + "', '" + endDate + "');");

                int seats = airlineRequested.getSeatsAvailable() - 1;

                String sql = "UPDATE airlines SET seats_available = "+ seats + " WHERE airline_name= '" + nameOfAirline + "';";
                stmt.executeUpdate(sql);
                stmt.close();

                SendMessageToProducer(guestName);
            } catch (Exception e) {
                System.out.println("Failed to add reservation.");
                e.printStackTrace();
            }

            reservations.put(guestName, new Reservation(airlineRequested.getFlightNum(),"Airline", guestName, nameOfAirline, startDate, endDate));
        } else {
            return "Airline not found or not available.  Please try a different airline.";
        }

        return "You reserved an Airline for guest: " + guestName + ", on airline: " + nameOfAirline;
    }

    private void SendMessageToProducer(String guestName) {
        ProducerRecord<String, String> recordToSend = new ProducerRecord<>("Airline", "Guest: "
                + guestName + " reserved an airline.");

        try {
            RecordMetadata metadata = producer.send(recordToSend).get();
            System.out.println("Message Sent!  Topic: " + metadata.topic() + "\n"
                + "Partition: " + metadata.partition());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String CancelAirlineReservation(String name) {
        System.out.println("CancelAirlineReservations function called.");

        reservations = GetAirlineReservations();
        airlines = GetAirlines();

        if (!reservations.containsKey(name)) {
            return "Reservation not found for guest: " + name;
        }

        Reservation airlineReservation = reservations.get(name);

        if (airlineReservation != null) {
            try {
                Airline airlineRequested = airlines.get(airlineReservation.getNameOfReservation());

                Statement stmt = connection.createStatement();
                String sql = "DELETE FROM reservations " +
                        "WHERE guest_name = '" + name + "' and reservation_type = " + "'Airline';";
                System.out.println(sql);

                int seats = airlineRequested.getSeatsAvailable() + 1;

                stmt.executeUpdate("UPDATE airlines " +
                        "SET seats_available = "+ seats + " WHERE airline_name= '" + airlineRequested.getName() +"';");

                stmt.close();
            } catch (Exception e) {
                System.out.println("Failed to cancel reservation.");
                e.printStackTrace();
            }

        } else {
            return "Reservation not found.  Please try a different reservation.";
        }

        return "You canceled an airline reservation for guest: " + name;
    }

    public void CloseConnection() {
        try {
            connection.close();
        } catch (Exception e) {
            System.out.println("Unable to close database connection.");
        }
    }

    public static void main(String args[]) {
        AirlineHelper airlineHelper = new AirlineHelper();
        try {
            airlineHelper.initializeRegistry();
            connection = airlineHelper.initializeDBConnection();
            //initializeDBData();

            System.out.println("AirlineServer waiting for client to connect...");
        } catch (SQLException e) {
            System.out.println("Cannot connect the database!");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("AirlineServer exception: " + e.toString());
        }
    }

    private static Producer<String, String> createProducer() {
        Properties kafkaProps = new Properties();
        kafkaProps.put("bootstrap.servers", "192.168.1.87:9092");
        kafkaProps.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        kafkaProps.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        return new KafkaProducer<>(kafkaProps);
    }
}