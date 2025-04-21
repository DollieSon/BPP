public class N_LiteralNode extends N_ASTNode {
    public final Object value;  // Can keep as Object since literals are leaves

    public N_LiteralNode(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}