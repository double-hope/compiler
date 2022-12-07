package compiler;

public class StatementWithExpression extends Statement{
    public Expression expression;

    protected StatementWithExpression(int row, int col, Expression expression) {
        super(row, col);
        this.expression = expression;
    }
}
