import java.io.Serializable;

public class Airline implements Serializable {
    String name;
    int flightNum;
    int seatsAvailable;
    String departureCity;
    String destinationCity;

    public Airline(String name, int flightNum, int seatsAvailable, String departureCity, String destinationCity) {
        setName(name);
        setFlightNum(flightNum);
        setSeatsAvailable(seatsAvailable);
        setDepartureCity(departureCity);
        setDestinationCity(destinationCity);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlightNum() {
        return flightNum;
    }

    public void setFlightNum(int flightNum) {
        this.flightNum = flightNum;
    }

    public int getSeatsAvailable() {
        return seatsAvailable;
    }

    public void setSeatsAvailable(int seatsAvailable) {
        this.seatsAvailable = seatsAvailable;
    }

    public String getDepartureCity() {
        return departureCity;
    }

    public void setDepartureCity(String departureCity) {
        this.departureCity = departureCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public void setDestinationCity(String destinationCity) {
        this.destinationCity = destinationCity;
    }
}
