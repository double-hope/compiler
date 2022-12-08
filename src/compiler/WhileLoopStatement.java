package compiler;

public class WhileLoopStatement extends StatementWithExpression{
    public Expression condition;

    public WhileLoopStatement(int row, int col, Expression condition) {
        super(row, col, condition);
    }
}
