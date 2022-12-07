package compiler;

public class ExpressionStatement extends StatementWithExpression{
    public ExpressionStatement(int row, int col, Expression expression) {
        super(row, col, expression);
    }
}
