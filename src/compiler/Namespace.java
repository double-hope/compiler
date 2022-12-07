package compiler;

import java.util.HashMap;
import java.util.List;

public abstract class Namespace implements INamespace{
    List<FuncStatement> funcList;
    HashMap<String, Integer> variables;
    public int varCounter;

    @Override
    public FuncStatement getFuncByName(String f) {
        FuncStatement function = null;
        for(FuncStatement func: funcList){
            if(func.name.equals(f)) function = func;
        }
        if(function == null)
            throw new IllegalArgumentException("Function {f} not found");
        return function;
    }
    @Override
    public boolean thereIsFuncWithName(String f) {
        return funcList.stream().anyMatch(func -> func.name.equals(f));
    }
    @Override
    public void addVariable(String varName) {
        if (variables.containsKey(varName)) return;
        varCounter++;
        String[] indexes = (String[]) variables.keySet().toArray();

        for (String index: indexes) {
            if(variables.get(index) > 0) variables.put(index, variables.get(index) + 4);
        }

        variables.put(varName, 4);
    }
}
