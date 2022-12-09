package compiler;

public class BinaryOperationExpression extends Expression{
    public TokenType operation;

    public Expression left;

    public Expression right;

    public BinaryOperationExpression(int row, int col, TokenType operation, Expression left, Expression right) {
        super(row, col);
        this.operation = operation;
        this.left = left;
        this.right = right;
    }

    @Override
    public void printOperation(int depth) {
        super.printOperation(depth);
        left.printOperation(depth + 1);
        System.out.println("\t" + operation);
        right.printOperation(depth + 1);
    }
}
