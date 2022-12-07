package compiler;

public class ReturnStatement extends StatementWithExpression{
    public Expression Return => Expression;
    public ReturnStatement(int row, int col, Expression returnExpression) {
        super(row, col, returnExpression);
    }
}
