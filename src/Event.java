// event class
public class Event implements Comparable {
    private double time;
    private EventType type;


    public Event(EventType type, double time) {
        this.type = type;
        this.time = time;
    }

    public EventType getType() {
        return this.type;
    }

    public double getTime() {
        return this.time;
    }

    // Define compareTo method. Compare time of event first; if time same, compare type of event then.
    // Define departure security < departure check in < arrival.
    // Make sure when we use peek() or pool() method of priority queue futureEventList,
    // a event with less time will be returned;
    // or a event with departure type will be returned earlier than arrival type if time same.
    public int compareTo(Object cmpEvent) {
        Event evt = (Event) cmpEvent;
        double cmp_time = evt.getTime();
        if( this.time < cmp_time) {
            return -1;
        }
        if( this.time == cmp_time) {
            return -1 * this.type.compareTo(evt.type);
        }
        return 1;
    }
}
