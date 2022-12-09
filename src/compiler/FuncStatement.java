package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class FuncStatement extends Statement implements IFunctions {
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
    public FuncStatement getFunctionByName(String f) {
        return null;
    }

    @Override
    public boolean thereIsFunctionWithName(String f) {
        return false;
    }
    @Override
    public void addVariable(String varName) {
        if (this.variables.containsKey(varName)) return;
        this.varCounter++;
        Set<String> indexes = variables.keySet();

        for (String index: indexes) {
            if(variables.get(index) > 0) variables.put(index, variables.get(index) + 4);
        }

        this.variables.put(varName, 4);
    }
}
