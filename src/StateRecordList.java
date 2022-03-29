import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class StateRecordList {
    private ArrayList<StateRecord> stateRecordList;
    private int totalTime;

    public StateRecordList() {
        this.stateRecordList = new ArrayList<>();
        this.totalTime = 0;
    }

    public ArrayList<StateRecord> getStateRecordList() {
        return stateRecordList;
    }

    public void addStateRecord(StateRecord record) {
        this.stateRecordList.add(record);
        this.totalTime = record.getClock();
        if(stateRecordList.size() >= 2) {
            this.totalTime = stateRecordList.get(stateRecordList.size() - 1).getClock() - stateRecordList.get(0).getClock();
        }
    }

    @Override
    public String toString() {
        return this.stateRecordList.toString();
    }
    // ADD FUNCTIONS TO CALCULATE STATISTICS
    public HashMap<ProductType, Double> calculateThroughput() {
        StateRecord lastRecord = this.stateRecordList.get(stateRecordList.size() - 1);
        HashMap<ProductType, Double> throughput = new HashMap<>();
        HashMap<ProductType, Integer> production = lastRecord.getProduction();
        for (Map.Entry<ProductType, Integer> entry : production.entrySet()) {
            ProductType key = entry.getKey();
            Integer value = entry.getValue();
            throughput.put(key, (double) value / ((double) totalTime / (double) 1000));
        }
        return throughput;
    }

    public HashMap<String, Double> calculateWorkstationBusyProbability() {
        // Iterate through record list, if workstation was busy before add the difference in clock
        // times (if it was idle before and busy now, that implies it just became busy as a result of the event, and
        // no time has passed for that busy cycle)
        HashMap<String, Integer> workstationBusyTime = new HashMap<>();
        Set<Workstation> keySet = stateRecordList.get(0).getWorkstationState().keySet();
        for(Workstation w : keySet) {
            workstationBusyTime.put(w.getName(), 0);
        }
        for(int i = 1; i < stateRecordList.size(); i++) {
            // Might be an issue here with references, we'll see
            int passedTime = stateRecordList.get(i).getClock() - stateRecordList.get(i-1).getClock();
            HashMap<Workstation, State> prevRecordWorkstationState = stateRecordList.get(i-1).getWorkstationState();
            for(Workstation w : keySet) {
                // If it was busy before, add the passed clock time to the corresponding hashmap entry
                if(prevRecordWorkstationState.get(w).equals(State.BUSY)) {
                    workstationBusyTime.put(w.getName(), workstationBusyTime.get(w.getName()) + passedTime);
                }
            }
        }
        HashMap<String, Double> workstationBusyProbability = new HashMap<>();
        for(Map.Entry<String, Integer> entry : workstationBusyTime.entrySet()) {
            workstationBusyProbability.put(entry.getKey(), (double) entry.getValue() / (double) totalTime);
        }
        return workstationBusyProbability;
    }

    public HashMap<String, Double> calculateBufferOccupancy() {
        // Iterate through record list, and sum up the weighted times of the buffer occupancy (num in buffer x elapsed
        // time), then divide by total time. Buffer occupancy is from the previous buffer state (since it is recorded
        // after the event)
        HashMap<String, Integer> bufferWeightedOccupancyTime = new HashMap<>();
        Set<Buffer> keySet = stateRecordList.get(0).getBufferOccupancy().keySet();
        for(Buffer b : keySet) {
            bufferWeightedOccupancyTime.put(b.getName(), 0);
        }
        for(int i = 1; i < stateRecordList.size(); i++) {
            int passedTime = stateRecordList.get(i).getClock() - stateRecordList.get(i-1).getClock();
            HashMap<Buffer, Integer> prevRecordBufferOccupancy = stateRecordList.get(i-1).getBufferOccupancy();
            for(Buffer b : keySet) {
                int weightedElapsedTime = prevRecordBufferOccupancy.get(b) * passedTime;
                bufferWeightedOccupancyTime.put(b.getName(), bufferWeightedOccupancyTime.get(b.getName()) + weightedElapsedTime);
            }
        }
        HashMap<String, Double> averageBufferOccupancy = new HashMap<>();
        for(Map.Entry<String, Integer> entry : bufferWeightedOccupancyTime.entrySet()) {
            averageBufferOccupancy.put(entry.getKey(), (double) entry.getValue() / (double) totalTime);
        }
        return averageBufferOccupancy;
    }

    public HashMap<String, Double> calculateInspectorIdleProbability() {
        // Iterate through record list, if inspector was idle before add the difference in clock
        // times (if it was busy before and idle now, that implies it just became idle as a result of the event, and
        // no time has passed for that idle cycle)

        HashMap<String, Integer> inspectorIdleTime = new HashMap<>();
        Set<Inspector> keySet = stateRecordList.get(0).getInspectorState().keySet();
        for(Inspector i : keySet) {
            inspectorIdleTime.put(i.getName(), 0);
        }

        for(int i = 1; i < stateRecordList.size(); i++) {
            int passedTime = stateRecordList.get(i).getClock() - stateRecordList.get(i-1).getClock();
            HashMap<Inspector, State> prevRecordInspectorState = stateRecordList.get(i-1).getInspectorState();
            for(Inspector insp : keySet) {
                // If it was idle before, add the passed clock time to the corresponding hashmap entry
                if(prevRecordInspectorState.get(insp).equals(State.IDLE)) {
                    inspectorIdleTime.put(insp.getName(), inspectorIdleTime.get(insp.getName()) + passedTime);
                }
            }
        }
        HashMap<String, Double> inspectorIdleProbability = new HashMap<>();
        for(Map.Entry<String, Integer> entry : inspectorIdleTime.entrySet()) {
            inspectorIdleProbability.put(entry.getKey(), (double) entry.getValue() / (double) totalTime);
        }
        return inspectorIdleProbability;
    }

    public void printAllStatisticsString() {
        HashMap<ProductType, Double> throughput = calculateThroughput();
        HashMap<String, Double> workstationBusyProbability = calculateWorkstationBusyProbability();
        HashMap<String, Double> bufferOccupancyAverage = calculateBufferOccupancy();
        HashMap<String, Double> inspectorIdleProbability = calculateInspectorIdleProbability();
        System.out.println("-----Throughput-----");
        for(Map.Entry<ProductType, Double> entry : throughput.entrySet()) {
            System.out.println(entry.getKey().toString() + ": " + entry.getValue());
        }
        System.out.println("-----Workstation Busy Probability-----");
        for(Map.Entry<String, Double> entry : workstationBusyProbability.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("-----Average Buffer Occupancy-----");
        for(Map.Entry<String, Double> entry : bufferOccupancyAverage.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println("-----Inspector Idle Probability-----");
        for(Map.Entry<String, Double> entry : inspectorIdleProbability.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
