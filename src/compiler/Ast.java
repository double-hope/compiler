package compiler;

import java.util.ArrayList;
import java.util.List;

public class Ast extends Functions {
    public int row;

    public int column;
    private List<Ast> children = new ArrayList<>();

    public void addChild(Ast child) {
        children.add(child);
    }

    public List<Ast> getChildren() {
        return children;
    }


    public Ast(int row, int col) {
        this.children = new ArrayList<>();
        this.row = row;
        this.column = col;
    }

    public Ast() { }
}
