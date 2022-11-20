enum ServerType {
    CHECK_IN,
    SECURITY_CHECK
}

public class Server {
    private final ServerType serverType;
    private int customersInService;
    private double busyTime;
    private int totalCustomersProcessed;
    private final int maxCustomers;

    public Server(ServerType type) {
        this.serverType = type;
        this.customersInService = 0;
        this.maxCustomers = 1;
        this.busyTime = 0;
        this.totalCustomersProcessed = 0;
    }

    public Server(ServerType type, int maxCustomers) {
        this.serverType = type;
        this.customersInService = 0;
        this.maxCustomers = maxCustomers;
        this.busyTime = 0;
        this.totalCustomersProcessed = 0;
    }

    public ServerType getServerType() {
        return this.serverType;
    }

    public boolean isAvailable() {
        return this.customersInService < maxCustomers;
    }


    public void serve() {
        // Simulate a serve process start when a departure event scheduled.
        // Set the server unavailable.
        if (isAvailable()) {
            this.customersInService++;
        }
    }

    public void finish() {
        // Simulate a serve process end when a departure event processed.
        // Set the server available.
        if (this.customersInService > 0) {
            this.customersInService--;
            this.totalCustomersProcessed++;
        }
    }

    public void logBusyTime(double time) {
        if (!this.isAvailable()) {
            this.busyTime += time;
        }
    }

    public double getBusyTime() {
        return this.busyTime;
    }

    public int getTotalCustomersProcessed() {
        return totalCustomersProcessed;
    }
}


