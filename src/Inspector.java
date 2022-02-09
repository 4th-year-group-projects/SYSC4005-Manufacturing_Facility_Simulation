import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;


public class Inspector {
    // Has production file(s) associated with its production
    // has a next method -> get the next production item
    // instance variables: array of files
    // convert to data structure when constructing
    // blockedState boolean -> workstation "notifies" inspectors when buffer becomes full or empty
    // random selection for first item of c2 and c3
    // method for putting components into buffers once component is inspected

    private HashMap<ComponentType, ArrayList<Double>> inspectionTimeArrays;
    private boolean blockedState;
    private int numComponents;
    public Inspector(HashMap<ComponentType, String> filenames) {
        this.numComponents = filenames.size();
        this.blockedState = false;
        for (ComponentType componentType : filenames.keySet()) {
            ArrayList<Double> array = Reader.readFile(filenames.get(componentType));
            this.inspectionTimeArrays.put(componentType, array);
        }
    }

    public void setBlockedState(Boolean state) {
        this.blockedState = state;
    }

    public Boolean getBlockedState() {
        return this.blockedState;
    }

    private ComponentType chooseNextComponent() {
        int index = Randomizer.getRandomNumber(numComponents);
        return new ArrayList<>(inspectionTimeArrays.keySet()).get(index);
    }

    private double getComponent(ComponentType type) {
        double time = inspectionTimeArrays.get(type).get(0);
        inspectionTimeArrays.get(type).remove(0);
        return time;
    }

    public Double getComponent1Time() {
        ArrayList<Double> comp1TimeArray = this.inspectionTimeArrays.get(ComponentType.C1);
        Double value = comp1TimeArray.get(0);
        comp1TimeArray.remove(0);
        return value;
    }

    public Double getComponent2Time() {
        ArrayList<Double> comp2TimeArray = this.inspectionTimeArrays.get(ComponentType.C2);
        Double value = comp2TimeArray.get(0);
        comp2TimeArray.remove(0);
        return value;
    }

    public Double getComponent3Time() {
        ArrayList<Double> comp3TimeArray = this.inspectionTimeArrays.get(ComponentType.C3);
        Double value = comp3TimeArray.get(0);
        comp3TimeArray.remove(0);
        return value;
    }
}
