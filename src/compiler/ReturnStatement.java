package compiler;

public class ReturnStatement extends StatementWithExpression{
    public Expression _return;
    public ReturnStatement(int row, int col, Expression returnExpression) {
        super(row, col, returnExpression);
    }
}
