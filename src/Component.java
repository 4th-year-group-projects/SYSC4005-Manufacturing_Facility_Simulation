public class Component {

    // Type: C1, C2, C3 -> assigned during construction -> an enum
    // getter for type

    private ComponentType type;

    public Component(ComponentType type) {
        this.type = type;
    }

    public ComponentType getType() {
        return this.type;
    }

    public void setType(ComponentType type) {
        this.type = type;
    }

}
