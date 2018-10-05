import java.io.Serializable;

public class Car implements Serializable {
    String carName;
    int id;
    String size;
    String model;
    int year;
    int numAvailable;

    public Car() {}

    public Car(String make, int id, String size, String model, int year, int qtyAvailable) {
        setCarMake(make);
        setId(id);
        setSize(size);
        setModel(model);
        setYear(year);
        setQtyAvailable(qtyAvailable);
    }

    public String getCarMake() {
        return carName;
    }

    public void setCarMake(String carName) {
        this.carName = carName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getQtyAvailable() {
        return numAvailable;
    }

    public void setQtyAvailable(int numAvailable) {
        this.numAvailable = numAvailable;
    }
}
