import java.util.ArrayList;
import java.util.HashMap;

public class StateRecord {
    private int clock; // capture time of record
    private EventType eventTrigger; // event that triggered save state
    private Workstation workstationSource; // Workstation that triggered event
    private Inspector inspectorSource; // Inspector that triggered event
    // Following corresponds to state AFTER triggering event executes
    private HashMap<Workstation, State> workstationState;
    private HashMap<Buffer, Integer> bufferOccupancy;
    private HashMap<Inspector, State> inspectorState;
    private HashMap<ProductType, Integer> production;

    public StateRecord(int clock, EventType eventType, Workstation w, Inspector i, Workstation[] workstations,
                       Inspector[] inspectors, HashMap<ProductType, Integer> production) {
        this.clock = clock;
        this.eventTrigger = eventType;
        this.production = new HashMap<>(production);
        this.workstationState = new HashMap<>();
        this.workstationSource = w;
        this.inspectorSource = i;
        this.bufferOccupancy = new HashMap<>();
        this.inspectorState = new HashMap<>();
        for(Workstation workstation : workstations) {
            this.workstationState.put(workstation, workstation.getState());
            for(Buffer b : workstation.getBuffers()) {
                bufferOccupancy.put(b, b.getSize());
            }
        }
        for(Inspector inspector : inspectors) {
            inspectorState.put(inspector, inspector.getState());
        }
    }

    @Override
    public String toString() {
        return "StateRecord{" +
                "time=" + clock +
                ", trigger=" + eventTrigger +
                ", workstationState=" + workstationState.toString() +
                ", inspSource=" + inspectorSource +
                ", stnSource=" + workstationSource +
                ", bufferOccupancy=" + bufferOccupancy.toString() +
                ", inspectorState=" + inspectorState.toString() +
                ", production=" + production.toString() +
                "}\n";
    }

    public int getClock() {
        return clock;
    }

    public Inspector getInspectorSource() {
        return inspectorSource;
    }

    public Workstation getWorkstationSource() {
        return workstationSource;
    }

    public EventType getEventTrigger() {
        return eventTrigger;
    }

    public HashMap<ProductType, Integer> getProduction() {
        return production;
    }

    public HashMap<Workstation, State> getWorkstationState() {
        return workstationState;
    }

    public HashMap<Buffer, Integer> getBufferOccupancy() {
        return bufferOccupancy;
    }

    public HashMap<Inspector, State> getInspectorState() {
        return inspectorState;
    }
}
