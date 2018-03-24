package pl.mrz;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.net.preferIPv4Stack", "true");
        new DistributedMap("distribute");
    }
}
