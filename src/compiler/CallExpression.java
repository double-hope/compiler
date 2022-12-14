package compiler;

import java.util.List;

public class CallExpression extends Expression {
    public String name;
    public List<Expression> args;

    public CallExpression(int row, int col, String name, List<Expression> args) {
        super(row, col);
        this.name = name;
        this.args = args;
    }

    @Override
    public void printOperation(int depth) {
        super.printOperation(depth);
        for (int i = 0; i <= depth; i++) {
            System.out.print('\t');
        }

        System.out.println(name);
    }
}
