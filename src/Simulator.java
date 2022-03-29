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
                    Workstation workstation = getWorkstationWithSmallestBuffer(workstations,
                            inspectorSource.getCurrentComponent());
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

    public StateRecordList getRecordList() {
        return recordList;
    }
}
