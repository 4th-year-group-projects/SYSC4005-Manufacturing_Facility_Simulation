
import java.util.concurrent.ArrayBlockingQueue;

public class Buffer {
    private final ComponentType type;
    private final ArrayBlockingQueue<Component> contents;
    private String name;

    public Buffer(String name, ComponentType type, int size) {
        this.name = name;
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

    public Boolean isEmpty() {
        return this.contents.isEmpty();
    }

    public String getName() {
        return name;
    }

    public Component popComponent() {
        try {
            return this.contents.take();
        } catch (InterruptedException e) {
            return null;
        }
    }
}
