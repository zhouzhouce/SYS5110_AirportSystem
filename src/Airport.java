import java.util.*;

public class Airport {
    // Class Sim variables
    private double clock, lastEventTime1, lastEventTime2, sumResponseTime;
    private int maxCheckInLength, maxSecCheckLength, longService;

    private int numTotalDeparture;
    private Random stream;
    private final PriorityQueue<Event> futureEventList;
    private final Queue<Event> waitingLineCheckIn;
    private final Queue<Event> waitingLineSecurity;
    private final Map<EventType, Server> servers;
    private final Server checkInServer1;
    private final Server checkInServer2;
    private final Server secCheckServer1;
    private final Server secCheckServer2;

    private final static int TOTAL_CUSTOMERS = 100;
    // Singleton class Airport
    private static Airport instance;

    private Airport() {
        clock = 0.0;
        lastEventTime1 = 0.0;
        lastEventTime2 = 0.0;
        maxCheckInLength = 0;
        maxSecCheckLength = 0;
        sumResponseTime = 0;
        longService = 0;
        checkInServer1 = new Server(ServerType.CHECK_IN);
        checkInServer2 = new Server(ServerType.CHECK_IN);
        secCheckServer1 = new Server(ServerType.SECURITY_CHECK);
        secCheckServer2 = new Server(ServerType.SECURITY_CHECK);

        futureEventList = new PriorityQueue<>();
        waitingLineCheckIn = new LinkedList<>();
        waitingLineSecurity = new LinkedList<>();
        servers = new HashMap<>(4);
        servers.put(EventType.DEPARTURE_CHECK_IN1, checkInServer1);
        servers.put(EventType.DEPARTURE_CHECK_IN2, checkInServer2);
        servers.put(EventType.DEPARTURE_SECURITY1, secCheckServer1);
        servers.put(EventType.DEPARTURE_SECURITY2, secCheckServer2);
    }

    public static Airport getInstance() {
        // Initialized a singleton airport instance
        if (instance == null) {
            instance = new Airport();
        }
        return instance;
    }

    private void initialization(long seed) {
        stream = new Random(seed);

        // create first arrival event
        Event evt = new Event(EventType.ARRIVAL, 0);
        futureEventList.add(evt);
    }

    public void simulate(long seed) {
        initialization(seed);

        // Loop until all customers have departed from security check point
        while(numTotalDeparture < TOTAL_CUSTOMERS && !futureEventList.isEmpty()) {
            Event evt = futureEventList.peek();
            futureEventList.poll();
            clock = evt.getTime();
            EventType type = evt.getType();
            switch (type) {
                case ARRIVAL:
                    processArrival(evt);
                    break;
                case DEPARTURE_CHECK_IN1:
                case DEPARTURE_CHECK_IN2:
                    processDepartureCheckIn(evt);
                    break;
                case DEPARTURE_SECURITY1:
                case DEPARTURE_SECURITY2:
                    processDepartureSecurity(evt);
                    break;
                default:
                    break;
            }
        }
    }

    private void processArrival(Event evt) {
        waitingLineCheckIn.add(evt);

        double busyTime = clock - lastEventTime1;
        // busy time will only be collected if server is not available
        checkInServer1.logBusyTime(busyTime);
        checkInServer2.logBusyTime(busyTime);

        // if the server is available, fetch the event, do statistics and put into service
        if(checkInServer1.isAvailable()) {
            scheduleDeparture(EventType.DEPARTURE_CHECK_IN1);
        } else if(checkInServer2.isAvailable()) {
            scheduleDeparture(EventType.DEPARTURE_CHECK_IN2);
        }

        // adjust max queue length statistics
        if (maxCheckInLength < waitingLineCheckIn.size()) {
            maxCheckInLength = waitingLineCheckIn.size();
        }

        // schedule the next arrival
        Event nextArrival = new Event(EventType.ARRIVAL, clock + Utils.generateArrivalInterval(stream));
        futureEventList.add(nextArrival);
        lastEventTime1 = clock;
    }

    private void processDepartureCheckIn(Event evt) {
        double busyTime = clock - lastEventTime1;
        processFinish(evt, busyTime);

        // get and pop the processed customer from waitingLineCheckIn
        Event finished = waitingLineCheckIn.poll();
        measureResponseTime(finished);

        // if there are customers in the queue then schedule another departure for the next customer (FIFO)
        if (!waitingLineCheckIn.isEmpty()) {
            if (checkInServer1.isAvailable()) {
                scheduleDeparture(EventType.DEPARTURE_CHECK_IN1);
            } else if (checkInServer2.isAvailable()) {
                scheduleDeparture(EventType.DEPARTURE_CHECK_IN2);
            }
        }

        lastEventTime1 = clock;

        // once the event of departure check in finished, it'll be added into the waitingLineSecurity (FIFO)
        // to indicate customers waiting for security check.
        waitingLineSecurity.add(evt);

        // busy time will only be collected if server is not available
        secCheckServer1.logBusyTime(busyTime);
        secCheckServer2.logBusyTime(busyTime);

        // if the server is available, fetch the event, do statistics and put into service
        if(secCheckServer1.isAvailable()) {
            scheduleDeparture(EventType.DEPARTURE_SECURITY1);
        } else if (secCheckServer2.isAvailable()) {
            scheduleDeparture(EventType.DEPARTURE_SECURITY2);
        }

        // adjust max queue length statistics
        if (maxSecCheckLength < waitingLineSecurity.size()) {
            maxSecCheckLength = waitingLineSecurity.size();
        }

        lastEventTime2 = clock;
    }

    private void processDepartureSecurity(Event evt) {
        double busyTime = clock - lastEventTime2;
        processFinish(evt, busyTime);

        Event finished = waitingLineSecurity.poll();
        measureResponseTime(finished);

        // if there are customers in the queue then schedule
        // the departure of the next one
        if (!waitingLineSecurity.isEmpty()) {
            if (secCheckServer1.isAvailable()) {
                scheduleDeparture(EventType.DEPARTURE_SECURITY1);
            } else if (secCheckServer2.isAvailable()) {
                scheduleDeparture(EventType.DEPARTURE_SECURITY2);
            }
        }
        // Add one to total departure number from security check point
        numTotalDeparture++;
        lastEventTime2 = clock;
    }

    private void processFinish(Event evt, double busyTime) {
        // Collect the busy time and set the server available when departure event processing.
        Server server = servers.get(evt.getType());
        if (server != null) {
            server.logBusyTime(busyTime);
            server.finish();
        }
    }

    private void scheduleDeparture(EventType type) {
        double ServiceTime = Utils.generateSeverTime(stream);
        // get the job at the head of the queue
        Event depart = new Event(type,clock + ServiceTime);
        futureEventList.add(depart);
        Server server = servers.get(type);
        if (server != null) {
            // Set the server unavailable.
            server.serve();
        }
    }

    private void measureResponseTime(Event evt) {
        if (evt != null) {
            // measure the response time, which is the time a customer spent in the system
            // including serve time and waiting time from arrival to finishing check in.
            double response = (clock - evt.getTime());
            sumResponseTime += response;
            if(response > 4.0) {
                longService++; // record long service
            }
        }
    }

    public void generateReport() {
        double totalBusy = servers.values().stream().mapToDouble(Server::getBusyTime).sum();
        double RHO   = totalBusy/(4*clock);
        double AVGR  = sumResponseTime/TOTAL_CUSTOMERS;
        double PC4   = ((double)longService)/TOTAL_CUSTOMERS;

        System.out.println("AIRPORT CHECK IN SYSTEM SIMULATION - GROCERY STORE CHECKOUT COUNTER ");
        System.out.println("\tNUMBER OF CUSTOMERS SERVED                     " + TOTAL_CUSTOMERS);
        System.out.println();
        System.out.println("\tSERVER UTILIZATION                             " + RHO );
        System.out.println("\tMAXIMUM LINE LENGTH FOR CHECK IN LINE          " + maxCheckInLength);
        System.out.println("\tMAXIMUM LINE LENGTH FOR SECURITY CHECK LINE    " + maxSecCheckLength);
        System.out.println("\tAVERAGE RESPONSE TIME                          " + AVGR + "  MINUTES");
        System.out.println("\tPROPORTION WHO SPEND FOUR ");
        System.out.println("\tMINUTES OR MORE IN SYSTEM                     " + PC4);
        System.out.println("\tSIMULATION RUNLENGTH                           " + clock + " MINUTES" );
    }
}
