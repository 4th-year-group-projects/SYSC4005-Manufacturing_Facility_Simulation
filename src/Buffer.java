import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

public class Buffer {
    private ComponentType type;
    private ArrayBlockingQueue<Component> contents;

    public Buffer(ComponentType type, int size) {
        this.type = type;
        contents = new ArrayBlockingQueue<>(size);
    }

    public ComponentType getType() {
        return this.type;
    }

    public int getSize() {
        return this.contents.size();
    }
    // Returns bool whether component was successfully added
    public Boolean addComponent(Component component) {
        if(component.getType().equals(type)) {
            this.contents.add(component); // Maybe change to put
            return true;
        }
        else return false;
    }

    public Component popComponent() {
        try {
            return this.contents.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
