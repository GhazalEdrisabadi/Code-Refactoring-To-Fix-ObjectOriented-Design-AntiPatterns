public class EventHandler { 
public void handle ( BankingEvent e ) { 
housekeeping ( e ) ; 
doHandle( e ); } 
public void handle ( BankingEvent e ) { 
housekeeping ( e ) ; 
} 
public void stop ( int a ) { 
} 
public void stop ( int a ) { 
doStop( a ); } 
protected void doHandle ( BankingEvent e ) {} protected void doStop ( int a ) {} } 
public class StartEventHandler extends EventHandler { 
@Override protected void doHandle ( BankingEvent e ) { 
initiateTransfer ( e ) ; 
} 
} 
public class TransferEventHandler extends EventHandler { 
@Override protected void doStop ( int a ) { 
} 
} 
