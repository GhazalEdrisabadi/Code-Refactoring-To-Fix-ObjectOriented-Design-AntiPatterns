/* The first mode */
/* This code has call super antipattern */
public class EventHandler{

    public void handle (BankingEvent e) {
        housekeeping(e);
    }
}

public class TransferEventHandler extends EventHandler{

    @Override
    public void handle(BankingEvent e) {
        super.handle(e);
        initiateTransfer(e);
    }
}

/* Refactored code */
/*
public class EventHandler{

    public void handle (BankingEvent e) {
        housekeeping(e);
        doHandle(e);
    }

    protected void doHandle(BankingEvent e){

    }
}

public class TransferEventHandler extends EventHandler{

    @Override
    protected void doHandle(BankingEvent e) {
        initiateTransfer(e);
    }
}
*/


