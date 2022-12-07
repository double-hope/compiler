package compiler;

public class ElseStatement extends ExpressionStatement{
    public Expression Condition -> Expression;

    public ElseStatement(int row, int col, Expression condition) {
        super(row, col, condition);
    }
}
