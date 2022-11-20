import java.util.Random;

public class Simulator {

    private static final long SEED = 1;

    public static void main(String argv[]) {
        Airport airport = Airport.getInstance();
        // Set up seed value for each simulation
        airport.simulate(SEED);
        airport.generateReport();
    }
}
