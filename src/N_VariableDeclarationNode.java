import java.util.ArrayList;
import java.util.List;

public class N_VariableDeclarationNode {
    public Tokenizer.Token_Enum type;
    public List<N_VariableNode> variables = new ArrayList<>();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VariableDeclaration (type: ").append(type).append("):\n");
        for (N_VariableNode var : variables) {
            sb.append("  ").append(var.toString()).append("\n");
        }
        return sb.toString();
    }
}
