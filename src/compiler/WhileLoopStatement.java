package compiler;

public class WhileLoopStatement extends StatementWithExpression{
    public Expression Condition => Expression;

    public WhileLoopStatement(int row, int col, Expression condition) {
        super(row, col, condition);
    }
}
