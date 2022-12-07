package compiler;

public class Statement extends Ast{
    protected Statement(int row, int col) {
        super(row, col);
    }
    public String name;
}
