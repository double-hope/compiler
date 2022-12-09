package compiler;

public interface IFunctions {
    FuncStatement getFuncByName(String f);
    boolean thereIsFuncWithName(String f);
    void addVariable(String varName);
}
