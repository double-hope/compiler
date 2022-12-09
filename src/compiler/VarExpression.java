package compiler;

public class VarExpression extends Expression{
    public String varName;

    public VarExpression(int row, int col, String data) {
        super(row, col);
        this.varName = data;
    }

    @Override
    public void printOperation(int depth) {
        super.printOperation(depth);
        for (int i = 0; i <= depth; i++) {
            System.out.print('\t');
        }

        System.out.println(varName);
    }
}
