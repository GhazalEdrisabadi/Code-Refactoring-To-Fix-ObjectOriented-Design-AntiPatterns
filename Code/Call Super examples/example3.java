/* The third mode */
/* This code has call super antipattern */
public class EventHandler {

    public void handle(BankingEvent e) {
        housekeeping(e);
    }

}

public class TransferEventHandler extends EventHandler {

    @Override
    public void handle(BankingEvent e) {
        super.handle(e);
        initiateTransfer(e);
    }
}

class Animal {

    public void display() {
        System.out.println("I am an animal");
    }
}

class Dog extends Animal {

    @Override
    public void display() {
        System.out.println("I am a dog");
        super.display();
    }

    public void printMessage() {

    }
}

class Main {
    public static void main(String[] args) {
        Dog dog1 = new Dog();
        dog1.printMessage();
    }
}

/* Refactored code */
/*
public class EventHandler {
    public void handle(BankingEvent e) {
        housekeeping(e);
        doHandle(e);
    }

    protected void doHandle(BankingEvent e) {
    }
}

public class TransferEventHandler extends EventHandler {
    @Override
    protected void doHandle(BankingEvent e) {
        initiateTransfer(e);
    }
}

class Animal {
    public void display() {
        System.out.println("I am an animal");
        doDisplay();
    }

    protected void doDisplay() {
    }
}

class Dog extends Animal {
    @Override
    protected void doDisplay() {
        System.out.println("I am a dog");
    }

    public void printMessage() {
    }
}

class Main {
    public static void main(String[] args) {
        Dog dog1 = new Dog();
        dog1.printMessage();
    }
}
*/
