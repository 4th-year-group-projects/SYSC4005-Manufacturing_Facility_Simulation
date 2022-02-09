import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class Workstation {
    private int priority;
    private ArrayList<ComponentType> componentTypes;
    private static final int BUFFER_SIZE = 2;
    private ArrayList<Buffer> buffers;

    public Workstation(ComponentType types[], int priority) {
        this.componentTypes = new ArrayList(Arrays.asList(types));
        this.priority = priority;
        buffers = new ArrayList<>();
        for (ComponentType type : types) {
            buffers.add(new Buffer(type, BUFFER_SIZE));
        }
    }

    public int getBufferCapacity(ComponentType type) {
        // Assumes that workstation will never have multiple buffers of the same component type
        for (Buffer buffer : buffers) {
            if (buffer.getType().equals(type)) {
                return buffer.getSize();
            }
        }
        return -1;
    }
}
