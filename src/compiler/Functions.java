package compiler;

import java.util.*;

public abstract class Functions implements IFunctions {
    List<FuncStatement> funcList = new ArrayList<>();
    HashMap<String, Integer> variables = new HashMap<>();
    public int varCounter;

    @Override
    public FuncStatement getFunctionByName(String f) {
        FuncStatement function = null;
        for(FuncStatement func: funcList){
            if(func.name.equals(f)) function = func;
        }
        if(function == null)
            throw new IllegalArgumentException(String.format("Function %s not found", f));
        return function;
    }
    @Override
    public boolean thereIsFunctionWithName(String f) {
        return funcList.stream().anyMatch(func -> func.name.equals(f));
    }
    @Override
    public void addVariable(String varName) {
        if (variables.containsKey(varName)) return;
        varCounter++;
        Set<String> indexes = variables.keySet();

        for (String index: indexes) {
            if(variables.get(index) > 0) variables.put(index, variables.get(index) + 4);
        }

        variables.put(varName, 4);
    }
}
