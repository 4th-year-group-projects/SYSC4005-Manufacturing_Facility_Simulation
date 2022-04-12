import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class Simulator {
    private int clock; // Clock time in thousandths of minutes
    private FutureEventList eventList;
    private HashMap<ProductType, Integer> production; // Final product counts
    private StateRecordList recordList;
    private Workstation[] workstations;
    private Inspector[] inspectors;
    private int iterations;
    private int numIterations = 5000; // CHANGE THIS FOR NUMBER OF ITERATIONS
    private static final Random random = new Random(66546215);
    private boolean useRNG;
    private static final int INITIALIZATION_TIME = 300000;

    public Simulator(Boolean useRNG) {
        this.useRNG = useRNG;
        this.iterations = 0;
        this.recordList = new StateRecordList();
        this.clock = 0;
        this.production = new HashMap<>();
        for (ProductType product : ProductType.values()) {
            production.put(product, 0);
        }
    }

    public void runRNGSimulations(int numSimulations, int numIterations) {
        this.numIterations = numIterations;
        for(int i = 0; i < numSimulations; i++) {
            reset();
            System.out.println("SIMULATION " + (i+1) + ":");
            simulate();
            recordList.printAllStatisticsString();
            System.out.println();
        }
    }

    private void reset() {
        this.iterations = 0;
        this.recordList = new StateRecordList();
        this.clock = 0;
        this.production = new HashMap<>();
        for (ProductType product : ProductType.values()) {
            production.put(product, 0);
        }
    }
    public void initializeNoRNG() {
        // Initialize workstations 1-3
        workstations = new Workstation[3];
        Workstation workstation1 = new Workstation("W1", new ComponentType[]{ComponentType.C1}, 3, ProductType.P1, "ws1.dat");
        Workstation workstation2 = new Workstation("W2", new ComponentType[]{ComponentType.C1, ComponentType.C2}, 2, ProductType.P2, "ws2.dat");
        Workstation workstation3 = new Workstation("W3", new ComponentType[]{ComponentType.C1, ComponentType.C3}, 1, ProductType.P3, "ws3.dat");
        workstations[0] = workstation1;
        workstations[1] = workstation2;
        workstations[2] = workstation3;

        // Initialize Inspectors 1 & 2
        inspectors = new Inspector[2];
        LinkedHashMap<ComponentType, String> insp1data = new LinkedHashMap<>();
        LinkedHashMap<ComponentType, String> insp2data = new LinkedHashMap<>();
        insp1data.put(ComponentType.C1, "servinsp1.dat");
        insp2data.put(ComponentType.C2, "servinsp22.dat");
        insp2data.put(ComponentType.C3, "servinsp23.dat");
        Inspector inspector1 = new Inspector("Insp1", insp1data);
        Inspector inspector2 = new Inspector("Insp2", insp2data);
        inspectors[0] = inspector1;
        inspectors[1] = inspector2;
    }

    public void initializeRNG() {
        long c1Seed = 6546524;
        long c2Seed = 165178;
        long c3Seed = 23187;
        long w1Seed = 6218926;
        long w2Seed = 2312159;
        long w3Seed = 23178651;

        // Initialize workstations 1-3
        workstations = new Workstation[3];
        Workstation workstation1 = new Workstation("W1", new ComponentType[]{ComponentType.C1}, 3, ProductType.P1, new RNGenerator(w1Seed, 1/4.60442));
        Workstation workstation2 = new Workstation("W2", new ComponentType[]{ComponentType.C1, ComponentType.C2}, 2, ProductType.P2, new RNGenerator(w2Seed, 1/11.0926));
        Workstation workstation3 = new Workstation("W3", new ComponentType[]{ComponentType.C1, ComponentType.C3}, 1, ProductType.P3, new RNGenerator(w3Seed, 1/8.79558));
        workstations[0] = workstation1;
        workstations[1] = workstation2;
        workstations[2] = workstation3;

        // Initialize Inspectors 1 & 2
        inspectors = new Inspector[2];
        LinkedHashMap<ComponentType, RNGenerator> insp1data = new LinkedHashMap<>();
        LinkedHashMap<ComponentType, RNGenerator> insp2data = new LinkedHashMap<>();
        insp1data.put(ComponentType.C1, new RNGenerator(c1Seed, 1/10.3579));
        insp2data.put(ComponentType.C2, new RNGenerator(c2Seed, 1/15.5369));
        insp2data.put(ComponentType.C3, new RNGenerator(c3Seed, 1/20.6328));
        Inspector inspector1 = new Inspector(insp1data, "Insp1");
        Inspector inspector2 = new Inspector(insp2data, "Insp2");
        inspectors[0] = inspector1;
        inspectors[1] = inspector2;
    }

    public void simulate() {
        if(useRNG) {
            initializeRNG();
        }
        else {
            initializeNoRNG();
        }
        this.eventList = initialize(inspectors);
        //StateRecord record = new StateRecord(clock, null, null, null, workstations, inspectors, production);
        //recordList.addStateRecord(record);
        // Action loop
        while(!eventList.isEmpty() && iterations < numIterations) {
            iterations++;
            Event currEvent = eventList.popEvent();
            this.clock = currEvent.getTime();
            //System.out.println(this.clock + ": Current[" + currEvent.toString() +"] FEL=" + this.eventList.toString());
            switch(currEvent.getType()) { // Yield?
                case INSPECTOR_FINISH -> {
                    Inspector inspectorSource = currEvent.getInspectorSource();
                    /** Substitute for alternative **/
                    Workstation workstation = null;
                    if (inspectorSource == inspectors[0]) {                                     // comment these out
                        workstation = getNextWorkstationAlternativeStrategy(workstations);      // comment these out
                    }                                                                           // comment these out
                    else {                                                                      // comment these out
                        workstation = getWorkstationWithSmallestBuffer(workstations,            // leave this
                                inspectorSource.getCurrentComponent());                         // leave this
                    }                                                                           // comment these out
                    /** Alternative selection ends here **/
                    // Workstation has space
                    if(workstation != null) {
                        workstation.addComponentToBuffer(inspectorSource.getCurrentComponent()); // Workstation gets component
                        generateInspectorEvent(inspectorSource);
                        // Check workstations if they can start new product job
                        if(workstation.canStartNewProduct()) {
                            generateWorkstationEvent(workstation);
                            for(Inspector inspector : inspectors) {
                                if(inspector.getState().equals(State.IDLE) &&
                                        workstation.needsComponent(inspector.getCurrentComponent())) {
                                    workstation.addComponentToBuffer(inspector.getCurrentComponent());
                                    generateInspectorEvent(inspector);
                                }
                            }
                        }
                    }
                    // No workstations have space
                    else {
                        inspectorSource.setState(State.IDLE);
                    }
                }
                case WORKSTATION_FINISH -> {
                    Workstation workstationSource = currEvent.getWorkstationSource();
                    production.put(workstationSource.getProduct(), production.get(workstationSource.getProduct()) + 1);
                    workstationSource.setState(State.IDLE);
                    // Workstation can start a new product (has all components) -> can open space in buffers
                    if(workstationSource.canStartNewProduct()) {
                        generateWorkstationEvent(workstationSource);
                        // Check idle inspectors for components, add to workstation
                        for(Inspector inspector : inspectors) {
                            if(inspector.getState().equals(State.IDLE) &&
                                    workstationSource.needsComponent(inspector.getCurrentComponent())) {
                                workstationSource.addComponentToBuffer(inspector.getCurrentComponent());
                                generateInspectorEvent(inspector);
                            }
                        }
                    }
                    // Workstation cannot start a new product (missing required component)
                    else {
                        // Don't think anything needs to happen
                    }

                }
            }
            if(clock >= INITIALIZATION_TIME) {
                StateRecord record = new StateRecord(clock, currEvent.getType(), currEvent.getWorkstationSource(), currEvent.getInspectorSource(), workstations, inspectors, production);
                recordList.addStateRecord(record);
            }



        }
        System.out.println("Final Production: " + production.toString());
        //System.out.println(recordList.toString());
        System.out.println("Iterations: " + iterations);
    }

    private void generateInspectorEvent(Inspector inspector) {
        if(inspector.isInspectionTimesEmpty()) return;
        int time = inspector.startNewComponent(); // Instructor starts new component
        Event newEvent = new Event(clock + time, EventType.INSPECTOR_FINISH, inspector);
        eventList.scheduleEvent(newEvent);
    }

    private void generateWorkstationEvent(Workstation workstation) {
        if(workstation.isProductionTimesEmpty()) return;
        int time = workstation.startNewProduct();
        Event newEvent = new Event(clock + time, EventType.WORKSTATION_FINISH, workstation);
        eventList.scheduleEvent(newEvent);
    }

    private FutureEventList initialize(Inspector[] inspectors) {
        // Should return a future event list of the initial inspector events
        FutureEventList initialList = new FutureEventList();
        for(Inspector inspector : inspectors) {
            Event newEvent = new Event(this.clock + inspector.startNewComponent(), EventType.INSPECTOR_FINISH, inspector);
            initialList.scheduleEvent(newEvent);
        }
        return initialList;
    }

    // This is the initial selection policy (and what we want to improve on)
    private Workstation getWorkstationWithSmallestBuffer(Workstation[] workstations, ComponentType component) {
        // Return the workstation with the smallest buffer / or highest priority if tied
        // Assumes that components will always have at least 1 assigned workstation
        // Returns null if no workstations available
        Workstation assignedWorkstation = null;
        int currWorkstationBufferOccupancy = Integer.MAX_VALUE;
        int currWorkstationPriority = -1;
        for(Workstation workstation : workstations) {
            if(workstation.needsComponent(component)) {
                if(workstation.getBufferOccupancy(component) <  currWorkstationBufferOccupancy &&
                        workstation.getPriority() > currWorkstationPriority) {
                    assignedWorkstation = workstation;
                }
            }
        }
        return assignedWorkstation;
    }

    // This is the alternative selection policy (for inspector 1), and is specifically implented for the 3 workstation,
    // 2 instructor
    private Workstation getNextWorkstationAlternativeStrategy(Workstation[] workstations) {
        // To avoid workstation idle times, we wish to select any workstations that can produce (other buffer
        // already has components in it (for inspector 1).
        // If workstations 2 and 3 have empty buffers, we put our components in workstation 1
        // Otherwise (or if workstation 1 is full) we put the component in whatever on whatever inspector 2 is working on
        Workstation assignedWorkstation = null;
        Workstation w1 = workstations[0];
        Workstation w2 = workstations[1];
        Workstation w3 = workstations[2];
        // Both w2 and w3 are empty, put a component in the one inspector 2 will add to
        if(w2.allBuffersEmpty() && w3.allBuffersEmpty()) {
            ComponentType goingToInspect = inspectors[1].getCurrentComponent();
            if(w2.usesComponent(goingToInspect)) {
                assignedWorkstation = w2;
            }
            else if(w3.usesComponent(goingToInspect)) {
                assignedWorkstation = w3;
            }
        }
        // w2 or w3 are not empty
        else {
            int w2compare = w2.compareBuffers(ComponentType.C1, ComponentType.C2);
            int w3compare = w3.compareBuffers(ComponentType.C1, ComponentType.C3);
            if(w2compare >= 0 && w3compare >= 0) { // Can do this since we know at least one has a component
                // Both workstations are either c1 dominant or equal, so try adding to w1
                if(w1.needsComponent(ComponentType.C1)) {
                    assignedWorkstation = w1;
                }
                else { // if c1 is full, choose the w2 or w3, whichever has smaller c1 buffer, w2 if tied, block if full
                    if(w2.needsComponent(ComponentType.C1)
                            && w2.getBufferOccupancy(ComponentType.C1) <= w3.getBufferOccupancy(ComponentType.C1)) {
                        assignedWorkstation = w2;
                    }
                    else if(w3.needsComponent(ComponentType.C1)) {
                        assignedWorkstation = w3;
                    }
                    else {
                        assignedWorkstation = null;
                    }
                }
            }
            else {
                if(w2compare <= w3compare && w2.needsComponent(ComponentType.C1)) {
                    assignedWorkstation = w2;
                }
                else if(w3.needsComponent(ComponentType.C1)) {
                    assignedWorkstation = w3;
                }
                else {
                    assignedWorkstation = null;
                }
            }
        }
        return assignedWorkstation;
    }

    public StateRecordList getRecordList() {
        return recordList;
    }
}
