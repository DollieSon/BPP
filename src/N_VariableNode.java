public class N_VariableNode extends N_ASTNode {
    public String name;
    public N_ASTNode value;  // Initialize as null for declarations without assignment

    // Add constructor
    public N_VariableNode(String name) {
        this.name = name;
        this.value = null;
    }

    // Optional: Add constructor with value for declarations with assignment
    public N_VariableNode(String name, N_ASTNode value) {
        this.name = name;
        this.value = value;
    }

    @Override
    public String toString() {
        if(value != null){
            return name + " " + value.getClass();
        }

        return name; // Simplified to just show the name
    }
}