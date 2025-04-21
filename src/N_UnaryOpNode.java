public class N_UnaryOpNode extends N_ASTNode {
    public final Token operator;  // e.g., -, DILI
    public final N_ASTNode operand;

    public N_UnaryOpNode(Token operator, N_ASTNode operand) {
        this.operator = operator;
        this.operand = operand;
    }

    @Override
    public String toString() {
        return String.format("(%s%s)",
                operator.keyword,
                operand.toString());
    }
}