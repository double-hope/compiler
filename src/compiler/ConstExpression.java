package compiler;

public class ConstExpression extends Expression{
    public String value;

    public ConstExpression(int row, int col, String value) {
        super(row, col);
        this.value = value;
    }

    @Override
    public void printOperation(int depth) {
        super.printOperation(depth);
        for (int i = 0; i <= depth; i++) {
            System.out.print('\t');
        }

        System.out.println(value);
    }
}
