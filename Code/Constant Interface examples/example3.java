/* The third mode */
/* This code has not constant interface */
public interface Vehicle {
    
    String getBrand();
    
    String speedUp();
    
    String slowDown();
    
    default String turnAlarmOn() {
        return "Turning the vehicle alarm on.";
    }
    
    default String turnAlarmOff() {
        return "Turning the vehicle alarm off.";
    }
}

public class Car {

    private String brand;
    
    public String getModel() {
		String model = "2008";
        return model;
    }
    
}