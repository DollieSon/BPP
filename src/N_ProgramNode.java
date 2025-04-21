// ast/ProgramNode.java

import java.util.ArrayList;
import java.util.List;

public class N_ProgramNode extends N_ASTNode {
    public List<N_ASTNode> statements = new ArrayList<>();  // Changed from Object to N_ASTNode

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program:\n");
        for (N_ASTNode stmt : statements) {
            sb.append("  ").append(stmt.toString().replace("\n", "\n  ")).append("\n");
        }
        return sb.toString();
    }
}