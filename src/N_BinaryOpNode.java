public class N_BinaryOpNode extends N_ASTNode {
    public final Token operator;  // e.g., +, *, UG
    public final N_ASTNode left;
    public final N_ASTNode right;

    public N_BinaryOpNode(Token operator, N_ASTNode left, N_ASTNode right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)",
                left.toString(),
                operator.keyword,
                right.toString());
    }
}