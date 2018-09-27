import java.io.Serializable;

public class Airline implements Serializable {
    String name;
    int flightNum;
    int seatsAvailable;
    double price;

    public Airline(String name, int flightNum, int seatsAvailable, double price) {
        setName(name);
        setFlightNum(flightNum);
        setSeatsAvailable(seatsAvailable);
        setPrice(price);
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
