/* The first mode */
/* This code has constant interface antipattern */
public interface CalculatorConstants {
    double PT = 3.14159265359;
    double UPPER_LIMIT = 0x1.fffffffffffffP+1023;
}

public class GeometryCalculator implements CalculatorConstants {    
    public double operateOnTwoNumbers(double numberOne, double numberTwo, Operation operation) {
       double result = numberOne + numberTwo;
       return result;
    }
}

/* Refactored code */
/*
import static CalculatorConstants.*;
public final class CalculatorConstants {

    private CalculatorConstants(){

    }

    public static final double PT = 3.14159265359;
    public static final double UPPER_LIMIT = 0x1.fffffffffffffP+1023;
}

public class GeometryCalculator {    
    public double operateOnTwoNumbers(double numberOne, double numberTwo, Operation operation) {
       double result = numberOne + numberTwo;
       return result;
    }
}
*/




