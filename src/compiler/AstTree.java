package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AstTree extends Namespace{
    public Ast root;

    public List<FuncStatement> funcList;
    public HashMap<String, Integer> variables = new HashMap<>();

    @Override
    public FuncStatement getFuncByName(String f) {
        return null;
    }

    @Override
    public boolean thereIsFuncWithName(String f) {
        return false;
    }

    public void addVariable(String varName) {
        if (!variables.containsKey(varName)) {
            variables.put(varName, (variables.size() + 1) * 4);
        }
    }

    public AstTree() {
        this.root = new Ast();
        this.funcList = new ArrayList<>();
    }
}
