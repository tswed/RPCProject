import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;

public class CarServer implements ICarServer {
    private HashMap<String, Car> cars = new HashMap<>();
    private HashMap<String, Reservation> reservations = new HashMap<>();

    public CarServer() {
        cars.put("Rav4", new Car("Toyota", 1, "Mid-Size", "Rav4", 2018, 3));
        cars.put("4Runner", new Car("Toyota", 2, "Huge", "4Runner", 2016, 4));
        cars.put("Taurus", new Car("Ford", 3, "Small", "Taurus", 2017, 15));
    }

    public HashMap<String, Car> GetCars() throws RemoteException {
        System.out.println("Serialized GetCars method called.");

        return cars;
    }

    public HashMap<String, Reservation> GetCarReservations() throws RemoteException {
        System.out.println("GetCarReservations function called.");
        return reservations;
    }

    public String AddCarReservation(String name, String carModel, String startDate, String endDate) throws RemoteException {
        System.out.println("AddCarReservations function called.");

        Car carRequested = cars.get(carModel);
        if (carRequested != null) {
            carRequested.setNumAvailable(carRequested.getNumAvailable() - 1);

            reservations.put(name, new Reservation("Car", name, carModel, startDate, endDate));
        } else {
            return "Car not found or not available.  Please try a different rental.";
        }

        return "You added a car reservation for car: " + carModel;
    }

    public String CancelCarReservation(String name) {
        System.out.println("CancelCarReservation function called.");

        Reservation carReservation = reservations.get(name);
        if (carReservation != null) {
            Car carRequested = cars.get(carReservation.getNameOfReservation());

            carRequested.setNumAvailable(carRequested.getNumAvailable() + 1);

            reservations.remove(name);
        } else {
            return "Car not found or not available.  Please try a different rental.";
        }

        return "You canceled a car reservation for: " + name;
    }

    public static void main(String args[]) {
        try {
            CarServer obj = new CarServer();
            ICarServer stub = (ICarServer) UnicastRemoteObject.exportObject(obj, 0);

            LocateRegistry.createRegistry(5003);
            Registry registry = LocateRegistry.getRegistry("192.168.56.1",5003);

            registry.bind("Car", stub);

            System.err.println("CarServer waiting for client to connect...");
        } catch (Exception e) {
            System.err.println("CarServer exception: " + e.toString());
            e.printStackTrace();
        }
    }
}