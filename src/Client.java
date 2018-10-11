import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

public class Client {

    public Client() {}
    static IHotelServer hotelStub;
    static IAirlineServer airlineStub;
    static ICarServer carStub;
    static KafkaConsumer<String, String> consumer;

    public static void main(String[] args) {

        try {
            //CallHotelServer();

        } catch (Exception e) {
            System.err.println("Communication with hotel server failed.");

            System.exit(1);
        }

        try {
            CallAirlineServer();

        } catch (Exception e){
            System.out.println("\n" + "Communication with airline server failed.");
            CancelHotelReservation(hotelStub, "hotel");

            System.exit(1);
        }

        try {
            //CallCarServer();

        } catch (Exception e) {
            System.err.println("Communication with car server failed.");
            CancelHotelReservation(hotelStub, "hotel");
            CancelAirlineReservation(airlineStub, "airline");

            System.exit(1);
        }

        CallConsumerCode();
    }

    private static void CallConsumerCode() {
        InitializeConsumer();

        consumer.subscribe(Collections.singletonList("Airline"));

        ConsumerRecords<String, String> records = consumer.poll(1000);
        for (ConsumerRecord<String, String> record : records) {
            System.out.println("Received message: (" + record.key() + ", " + record.value() + ") at offset " + record.offset());
        }
    }

    private static void InitializeConsumer() {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.168.1.87:9092");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "Client");
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
        props.put(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "1000");
        props.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "30000");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerDeserializer");
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

        consumer = new KafkaConsumer<>(props);
    }

    private static void CancelAirlineReservation(IAirlineServer airlineStub, String type) {
        try {
            System.out.println(airlineStub.CancelAirlineReservation("Mr. Incredible"));

        } catch (Exception e) {
            System.out.println("Unable to cancel " + type + " reservation.");
        }
    }

    private static void CancelHotelReservation(IHotelServer hotelStub, String type) {
        try {
            System.out.println(hotelStub.CancelHotelReservation("Mr. Incredible"));

        } catch (Exception e) {
            System.out.println("Unable to cancel " + type + " reservation.");
        }
    }

    private static void CallAirlineServer() throws Exception {
        Registry airlineRegistry = LocateRegistry.getRegistry("192.168.56.1", 5002);
        airlineStub = (IAirlineServer) airlineRegistry.lookup("Airline");

        HashMap<String, Reservation> reservations;
        HashMap<String, Airline> airlines;

        airlines = airlineStub.GetAirlines();

        for (Airline airline : airlines.values()) {
            System.out.print("Available airline: " + airline.getName() + ", Number of seats available: "
                + airline.getSeatsAvailable() + "\n");
        }

        System.out.println(airlineStub.AddAirlineReservation("Mr. Incredible", "United", "01-01-2015", "01-15-2019"));

        reservations = airlineStub.GetAirlineReservations();
        DisplayReservations(reservations);

        System.out.println(airlineStub.CancelAirlineReservation("Mr. Incredible"));

        System.out.println();
    }

    private static void CallHotelServer() throws Exception {
        Registry hotelRegistry = LocateRegistry.getRegistry("172.22.181.50", 5001);
        hotelStub = (IHotelServer) hotelRegistry.lookup("Hotel");
        HashMap<String, Reservation> reservations;
        HashMap<String, Hotel> hotels;

        hotels = hotelStub.GetHotels();

        for(Hotel hotel : hotels.values()) {
            System.out.print("Available hotel: " + hotel.getHotelName() + ", Number of rooms available: "
                    + hotel.getNumOfRoomsAvailable() + ", Price: " + hotel.getPrice() + "\n");
        }

        System.out.println(hotelStub.AddHotelReservation("Mr. Incredible", "Marriott", "01-01-2019", "01-15-2019"));

        reservations = hotelStub.GetHotelReservations();
        DisplayReservations(reservations);

        System.out.println(hotelStub.CancelHotelReservation("Mr. Incredible"));

        System.out.println();
    }

    private static void DisplayReservations(HashMap<String,Reservation> reservations) {
        for (Reservation reservation : reservations.values()) {
            System.out.print("Name on reservation: " + reservation.getNameOfGuest() + ", type: " + reservation.getType()
                    + ", StartDate: " + reservation.getStartDate() + ", EndDate: " + reservation.getEndDate() + "\n" );
        }
    }

    private static void CallCarServer() throws Exception {
        Registry carRegistry = LocateRegistry.getRegistry("172.22.181.30", 5003);
        carStub = (ICarServer) carRegistry.lookup("Car");

        HashMap<String, Car> cars;
        HashMap<String, Reservation> reservations;

        cars = carStub.GetCars();

        for(Car car : cars.values()) {
            System.out.print("Available car: " + car.getCarMake() + ", Model: " + car.getModel() + ", Year: "
                + car.getYear() + ", Number Available: " + car.getQtyAvailable() + "\n");
        }

        System.out.println(carStub.AddCarReservation("Mr. Incredible", "4Runner", "01-01-2019", "01-15-2019"));

        reservations = carStub.GetCarReservations();
        DisplayReservations(reservations);
        System.out.println(carStub.CancelCarReservation("Mr. Incredible"));

        System.out.println();
    }
}