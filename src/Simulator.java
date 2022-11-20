import java.util.Random;

public class Simulator {

    public static void main(String argv[]) {
        Airport airport = Airport.getInstance();
        // Set up seed value for each simulation
        int seed = 1;
        airport.simulate(seed);
        airport.generateReport();
    }
}
