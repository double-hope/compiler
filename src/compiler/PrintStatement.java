package compiler;

public class PrintStatement extends StatementWithExpression{
    public PrintStatement(int row, int col, Expression expression) {
        super(row, col, expression);
    }
}
