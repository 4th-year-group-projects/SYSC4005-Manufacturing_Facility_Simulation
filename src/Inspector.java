import java.util.ArrayList;
import java.util.HashMap;


public class Inspector {

    private HashMap<ComponentType, ArrayList<Integer>> inspectionTimes;
    private State state;
    private int numComponents;
    private ComponentType currentComponent;

    public Inspector(HashMap<ComponentType, String> filenames) {
        this.numComponents = filenames.size();
        this.state = State.IDLE;
        inspectionTimes = new HashMap<>();
        for (ComponentType componentType : filenames.keySet()) {
            ArrayList<Integer> array = Reader.readFile(filenames.get(componentType));
            this.inspectionTimes.put(componentType, array);
        }
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
        return new ArrayList<>(inspectionTimes.keySet()).get(index);
    }

    private int getComponentTime(ComponentType type) {
        int time = inspectionTimes.get(type).get(0);
        inspectionTimes.get(type).remove(0);
        if(inspectionTimes.get(type).isEmpty()) {
            numComponents--;
            inspectionTimes.remove(type);
        }
        return time;
    }

    public boolean isInspectionTimesEmpty() {
        return this.inspectionTimes.isEmpty();
    }
}
