import static CalculatorConstants.*;
public final class CalculatorConstants { 
private CalculatorConstants () { 
} 
public static final double PT=3.14159265359 ; 
public static final double UPPER_LIMIT=0x1.fffffffffffffP+1023 ; 
} 
public class GeometryCalculator { 
public double operateOnTwoNumbers ( double numberOne , double numberTwo , Operation operation ) { 
double result = numberOne + numberTwo ; 
return result ; 
} 
} 
