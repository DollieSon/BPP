import java.util.ArrayList;
import java.util.List;

public class N_PrintNode extends N_ASTNode {
    private final List<N_ASTNode> expressions;  // Changed from Object to N_ASTNode

    public N_PrintNode() {
        this.expressions = new ArrayList<>();
    }

    public void addExpression(N_ASTNode expr) {
        expressions.add(expr);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Print:\n");
        for (N_ASTNode expr : expressions) {
            sb.append(expr);
        }
        return sb.toString();
    }
}