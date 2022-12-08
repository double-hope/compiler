package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FuncStatement extends Statement implements INamespace{
    public List<String> args;
    public HashMap<String, Integer> variables;
    public List<FuncStatement> funcList;
    public int varCounter;
    public Expression _return;
    public FuncStatement(int row, int col, HashMap<String, Integer> varTable) {
        super(row, col);
        this.args = new ArrayList<>();
        this.variables = varTable;
        this.funcList = new ArrayList<>();
    }

    public void addArg(String argName) {
        int value = 0;

        for (String key: this.variables.keySet()) {
            if (this.variables.get(key) < 0) value++;
        }

        this.variables.put(argName, -(value + 2) * 4);
    }

    @Override
    public FuncStatement getFuncByName(String f) {
        return null;
    }

    @Override
    public boolean thereIsFuncWithName(String f) {
        return false;
    }
    @Override
    public void addVariable(String varName) {
        if (this.variables.containsKey(varName)) return;
        this.varCounter++;
        String[] indexes = (String[]) variables.keySet().toArray();

        for (String index: indexes) {
            if(variables.get(index) > 0) variables.put(index, variables.get(index) + 4);
        }

        this.variables.put(varName, 4);
    }
}
