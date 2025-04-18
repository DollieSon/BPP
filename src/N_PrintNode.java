import java.util.ArrayList;
import java.util.List;

public class N_PrintNode {
    private final List<Object> expressions; // Can be variables, strings, or special symbols ($, [])

    // In ProgramNode.java
    private List<Object> statements = new ArrayList<>();

    public void addPrintStatement(N_PrintNode printNode) {
        statements.add(printNode);
    }

    public N_PrintNode() {
        this.expressions = new ArrayList<>();
    }

    public void addExpression(Object expr) {
        expressions.add(expr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Print:\n");
        for (Object expr : expressions) {
            sb.append("  ").append(expr).append("\n");
        }
        return sb.toString();
    }
}