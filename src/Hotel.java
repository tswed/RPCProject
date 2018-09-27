import java.io.Serializable;

public class Hotel implements Serializable {
    private String hotelName;
    private int numOfRoomsAvailable;
    private double price;

    public Hotel(String name, int numRooms, double price) {
        setHotelName(name);
        setNumOfRoomsAvailable(numRooms);
        setPrice(price);
    }

    public String getHotelName() {
        return hotelName;
    }

    private void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public int getNumOfRoomsAvailable() {
        return numOfRoomsAvailable;
    }

    void setNumOfRoomsAvailable(int numOfRoomsAvailable) {
        this.numOfRoomsAvailable = numOfRoomsAvailable;
    }

    public double getPrice() {
        return price;
    }

    private void setPrice(double price) {
        this.price = price;
    }
}
