import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Workstation {
    private int priority; // Higher number is higher priority
    private ArrayList<Integer> productionTimes;
    private State state;
    private ProductType product;
    private ArrayList<ComponentType> componentTypes;
    private static final int BUFFER_SIZE = 2;
    private ArrayList<Buffer> buffers;
    private String name;
    private Boolean useRNG;
    private RNGenerator productionTimeGenerator;

    // Assumes that all workstation will have at least 1 buffer
    public Workstation(String name, ComponentType types[], int priority, ProductType product, String filename) {
        this.useRNG = false;
        this.name = name;
        this.productionTimes = Reader.readFile(filename);
        this.productionTimeGenerator = null;
        this.product = product;
        this.state = State.IDLE;
        this.componentTypes = new ArrayList(Arrays.asList(types));
        this.priority = priority;
        buffers = new ArrayList<>();
        for (ComponentType type : types) {
            buffers.add(new Buffer(name + " " + type.toString(), type, BUFFER_SIZE));
        }
    }

    // Assumes that all workstation will have at least 1 buffer
    public Workstation(String name, ComponentType types[], int priority, ProductType product, RNGenerator generator) {
        this.useRNG = true;
        this.productionTimeGenerator = generator;
        this.name = name;
        this.productionTimes = null;
        this.product = product;
        this.state = State.IDLE;
        this.componentTypes = new ArrayList(Arrays.asList(types));
        this.priority = priority;
        buffers = new ArrayList<>();
        for (ComponentType type : types) {
            buffers.add(new Buffer(name + " " + type.toString(), type, BUFFER_SIZE));
        }
    }

    public int compareBuffers(ComponentType type1, ComponentType type2) {
        Buffer b1 = null;
        Buffer b2 = null;
        for (Buffer b : buffers) {
            if(b.getType() == type1) b1 = b;
            if(b.getType() == type2) b2 = b;
        }
        return b1.getSize() - b2.getSize();
    }

    public boolean needsComponent(ComponentType type) {
        for(Buffer buffer : buffers) {
            if(buffer.getType().equals(type) && buffer.getSize() < BUFFER_SIZE) {
                return true;
            }
        }
        return false;
    }

    public int getBufferOccupancy(ComponentType type) {
        // Assumes that workstation will never have multiple buffers of the same component type
        for (Buffer buffer : buffers) {
            if (buffer.getType().equals(type)) {
                return buffer.getSize();
            }
        }
        return -1;
    }

    public boolean allBuffersEmpty() {
        for (Buffer b : buffers) {
            if (!b.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public boolean usesComponent(ComponentType type) {
        return this.componentTypes.contains(type);
    }

    public int[] getAllBufferOccupancies() {
        int[] occupancies = new int[buffers.size()];
        for (int i = 0; i < buffers.size(); i++) {
            occupancies[i] = buffers.get(i).getSize();
        }
        return occupancies;
    }

    public void addComponentToBuffer(ComponentType component) {
        // Assumes that component exists in this workstation
        for (Buffer buffer : buffers) {
            if(buffer.getType().equals(component)) {
                buffer.addComponent(new Component(component));
            }
        }
    }

    public int getPriority() {
        return priority;
    }

    public boolean canStartNewProduct() {
        if (this.state.equals(State.BUSY)) return false;
        for (Buffer buffer : buffers) {
            if (buffer.getSize() == 0) {
                return false;
            }
        }
        return true;
    }

    public int startNewProduct() {
        // Returns the time this component will take to complete (thousandths of minutes)
        // Removes components from buffers
        this.state = State.BUSY;
        for (Buffer buffer : buffers) {
            buffer.popComponent();
        }
        return getProductTime();
    }

    private int getProductTime() {
        int time;
        if(!useRNG) {
            time = productionTimes.get(0);
            productionTimes.remove(0);
        }
        else {
            time = (int) (productionTimeGenerator.getNext() * 1000);
        }
        return time;
    }

    public void setState(State state) {
        this.state = state;
    }

    public ArrayList<Buffer> getBuffers() {
        return this.buffers;
    }

    public State getState() {
        return this.state;
    }

    public ProductType getProduct() {
        return product;
    }

    public ArrayList<ComponentType> getComponentTypes() {
        return componentTypes;
    }

    public boolean isProductionTimesEmpty() {
        if(!useRNG) {
            return this.productionTimes.isEmpty();
        }
        else {
            return false;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
