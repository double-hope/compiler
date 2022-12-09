package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AstTree extends Functions {
    public Ast root;

    public List<FuncStatement> functionsList;
    public HashMap<String, Integer> variables = new HashMap<>();

    @Override
    public FuncStatement getFunctionByName(String f) {
        return null;
    }

    @Override
    public boolean thereIsFunctionWithName(String f) {
        return false;
    }

    public void addVariable(String varName) {
        if (!variables.containsKey(varName)) {
            variables.put(varName, (variables.size() + 1) * 4);
        }
    }

    public AstTree() {
        this.root = new Ast();
        this.functionsList = new ArrayList<>();
    }
}
