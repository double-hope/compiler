package compiler;

public class Expression extends Ast{
    public Expression(int row, int col){
        super(row, col);
    }

    public void PrintOperation(int depth) {
        for (var i = 0; i < depth; i++) {
            System.out.print('\t');
        }

        System.out.println(getClass().toString());
    }
}
