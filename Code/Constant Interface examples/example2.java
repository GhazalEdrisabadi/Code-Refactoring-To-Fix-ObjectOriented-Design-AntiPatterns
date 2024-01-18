/* The second mode */
/* This code has constant interface */
public interface OlympicMedal {
    static final String GOLD = "Gold";
    static final String SILVER = "Silver";
    static final String BRONZE = "Bronze";
}

public class OlympicAthlete implements OlympicMedal {

    private String medal;
    
    public OlympicAthlete(int id){

    }

    public void winEvent(){
        medal = GOLD;
    }
}

/* Refactored code */
/*
import static OlympicMedal.*;

public final class OlympicMedal {

    private OlympicMedal(){

    }

    static final String GOLD = "Gold";
    static final String SILVER = "Silver";
    static final String BRONZE = "Bronze";
}

public class OlympicAthlete {

    private String medal;

    public OlympicAthlete(int id){

    }

    public void winEvent(){
        medal = GOLD;
    }    
} 
 */