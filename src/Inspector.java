import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;


public class Inspector {
    private HashMap<ComponentType, ArrayList<Integer>> inspectionFileTimes;
    private HashMap<ComponentType, RNGenerator> inspectionRandomizers;
    private Set<ComponentType> components;
    private State state;
    private int numComponents;
    private ComponentType currentComponent;
    private String name;
    private Boolean useRNG;

    public Inspector(String name, HashMap<ComponentType, String> filenames) {
        this.name = name;
        this.components = filenames.keySet();
        this.numComponents = filenames.size();
        this.state = State.IDLE;
        inspectionFileTimes = new HashMap<>();
        for (ComponentType componentType : filenames.keySet()) {
            ArrayList<Integer> array = Reader.readFile(filenames.get(componentType));
            this.inspectionFileTimes.put(componentType, array);
        }
        this.useRNG = false;
    }

    public Inspector(HashMap<ComponentType, RNGenerator> arrivalRates, String name) {
        this.name = name;
        this.components = arrivalRates.keySet();
        this.inspectionRandomizers = arrivalRates;
        this.numComponents = arrivalRates.size();
        this.state = State.IDLE;
        this.useRNG = true;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return this.state;
    }
    
    public int startNewComponent() {
        // Returns the time this component will take to complete (thousandths of minutes)
        this.state = State.BUSY;
        this.currentComponent = chooseNextComponent();
        return getComponentTime(this.currentComponent);
    }

    public ComponentType getCurrentComponent() {
        return currentComponent;
    }

    private ComponentType chooseNextComponent() {
        int index = Randomizer.getRandomNumber(numComponents);
        return new ArrayList<>(components).get(index);
    }

    private int getComponentTime(ComponentType type) {
        int time;
        if(!useRNG) {
            time = inspectionFileTimes.get(type).get(0);
            inspectionFileTimes.get(type).remove(0);
            if(inspectionFileTimes.get(type).isEmpty()) {
                numComponents--;
                inspectionFileTimes.remove(type);
            }
        }
        else {
            time = (int) (inspectionRandomizers.get(type).getNext() * 1000);
        }
        return time;
    }

    public String getName() {
        return name;
    }

    public boolean isInspectionTimesEmpty() {
        if(!useRNG) {
            return this.inspectionFileTimes.isEmpty();
        }
        else {
            return false;
        }
    }
}
