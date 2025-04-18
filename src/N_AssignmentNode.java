public class N_AssignmentNode {
    private final String variableName;
    private final Object value;

    public N_AssignmentNode(String variableName, Object value) {
        this.variableName = variableName;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Assignment: %s = %s", variableName, value);
    }
}
