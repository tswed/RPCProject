import java.io.Serializable;

public class Reservation implements Serializable {
    private String type;
    private String nameOfReservation;
    private String nameOfGuest;
    private String startDate;
    private String endDate;
    private int id;

    public Reservation(int id, String type, String nameOfGuest, String nameOfReservation, String startDate, String endDate) {
        setId(id);
        setType(type);
        setNameOfReservation(nameOfReservation);
        setNameOfGuest(nameOfGuest);
        setStartDate(startDate);
        setEndDate(endDate);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameOfReservation() {
        return nameOfReservation;
    }

    public void setNameOfReservation(String nameOfReservation) {
        this.nameOfReservation = nameOfReservation;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNameOfGuest() {
        return nameOfGuest;
    }

    public void setNameOfGuest(String nameOfGuest) {
        this.nameOfGuest = nameOfGuest;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }
}
