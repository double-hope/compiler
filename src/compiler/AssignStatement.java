package compiler;

public class AssignStatement extends StatementWithExpression{
    public String varName;

    public AssignStatement(int row, int col, String name, Expression e) {
        super(row, col, e);
        this.varName = name;
    }
}
