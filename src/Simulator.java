import java.util.Random;

public class Simulator {

    private static final long SEED = 3;

    public static void main(String argv[]) {
        Airport airport = Airport.getInstance();
        // Set up seed value for each simulation
        //long seed = Long.parseLong(argv[0]);
        airport.simulate(SEED);
        airport.generateReport();
    }
}
