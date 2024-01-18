/* The second mode */
/* This code has call super antipattern */
public class EventHandler{

    public void handle (BankingEvent e) {
        housekeeping(e);
    }

    public void start (BankingEvent e) {
        string s = "";
        switch (e.number) {
            case 1:
                s = "step 1";
                break;

            case 2:
                s = "step 2";
                break;
        
            default:
                break;
        }
    }
}

public class TransferEventHandler extends EventHandler{

    public void transfer (BankingEvent e) {
        
    }

    @Override
    public void handle(BankingEvent e) {
        super.handle(e);
        initiateTransfer(e);
    }

    @Override
    public void start (BankingEvent e) {
        transfer(e);
    }
}

/* Refactored code */
/*
public class EventHandler{

    public void handle (BankingEvent e) {
        housekeeping(e);
        doHandle(e);
    }

    public void start (BankingEvent e) {
        string s = "";
        switch (e.number) {
            case 1:
                s = "step 1";
                break;

            case 2:
                s = "step 2";
                break;
        
            default:
                break;
        }
    }

    protected void doHandle(BankingEvent e){

    }
}

public class TransferEventHandler extends EventHandler{

    public void transfer (BankingEvent e) {
        
    }

    @Override
    protected void doHandle(BankingEvent e) {
        initiateTransfer(e);
    }

    @Override
    public void start (BankingEvent e) {
        transfer(e);
    }
}
*/