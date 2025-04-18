public class N_AssignmentNode extends N_ASTNode {  // Now extends N_ASTNode
    private final String variableName;
    private final N_ASTNode value;  // Changed from Object to N_ASTNode

    public N_AssignmentNode(String variableName, N_ASTNode value) {
        this.variableName = variableName;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("Assignment: %s = %s", variableName, value);
    }
}