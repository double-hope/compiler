package compiler;

public class IfStatement extends StatementWithExpression{
    public Expression condition -> Expression;

    public IfStatement(int row, int col,
                       Expression condition) {
        super(row, col, condition);
    }
}
