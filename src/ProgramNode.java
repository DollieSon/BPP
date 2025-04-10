// ast/ProgramNode.java

import java.util.ArrayList;
import java.util.List;

public class ProgramNode {
    public List<Object> statements = new ArrayList<>(); // Store any statement type here

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program:\n");
        for (Object stmt : statements) {
            sb.append("  ").append(stmt.toString().replace("\n", "\n  ")).append("\n");
        }
        return sb.toString();
    }
}