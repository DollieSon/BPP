public class VariableNode {
    public String name;
    public Object value; // Integer, Character, Boolean, etc.

    // In VariableNode.java
    @Override
    public String toString() {
        return "Variable: " + name + (value != null ? " = " + value : "");
    }
}
