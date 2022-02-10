public class Event {
    private final int time;
    private final EventType type;
    private Inspector inspectorSource;
    private Workstation workstationSource;

    private Event(int time, EventType type) {
        this.time = time;
        this.type = type;
    }

    public Event(int time, EventType type, Inspector inspectorSource) {
        this(time, type);
        this.inspectorSource = inspectorSource;
        this.workstationSource = null;
    }

    public Event(int time, EventType type, Workstation workstationSource) {
        this(time, type);
        this.inspectorSource = null;
        this.workstationSource = workstationSource;
    }

    public int getTime() {
        return this.time;
    }

    public EventType getType() {
        return this.type;
    }

    public Inspector getInspectorSource() {
        return this.inspectorSource;
    }

    public Workstation getWorkstationSource() {
        return this.workstationSource;
    }

    @Override
    public String toString() {
        return "Event{" +
                "time=" + time +
                ", type=" + type +
                ", inspectorSource=" + inspectorSource +
                ", workstationSource=" + workstationSource +
                '}';
    }
}
