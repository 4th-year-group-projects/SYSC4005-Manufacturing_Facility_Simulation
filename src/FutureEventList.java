import java.util.ArrayList;

public class FutureEventList {
    private ArrayList<Event> list;

    public FutureEventList() {
        this.list = new ArrayList<>();
    }

    // Should schedule events in order of time
    public void scheduleEvent(Event event) {
        // Empty event list
        if(this.list.isEmpty()) {
            list.add(event);
            return;
        }
        // Event fits into middle of list
        for (int i = 0; i < list.size(); i++) {
            if(event.getTime() < list.get(i).getTime()) {
                list.add(i, event);
                return;
            }
        }
        // Event is latest time in event
        list.add(event);
    }

    public Event popEvent() {
        Event event = list.get(0);
        list.remove(0);
        return event;
    }

    public boolean isEmpty() {
        return this.list.isEmpty();
    }

    @Override
    public String toString() {
        return this.list.toString();
    }
}
