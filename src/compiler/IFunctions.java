package compiler;

public interface IFunctions {
    FuncStatement getFunctionByName(String f);
    boolean thereIsFunctionWithName(String f);
    void addVariable(String varName);
}
