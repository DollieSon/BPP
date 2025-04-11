import java.util.ArrayList;
import java.util.List;

public class PrintNode {
    private final List<Object> expressions; // Can be variables, strings, or special symbols ($, [])

    // In ProgramNode.java
    private List<Object> statements = new ArrayList<>();

    public void addPrintStatement(PrintNode printNode) {
        statements.add(printNode);
    }

    public PrintNode() {
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