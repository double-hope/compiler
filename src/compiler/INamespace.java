package compiler;

public interface INamespace {

    FuncStatement getFuncByName(String f);
    boolean thereIsFuncWithName(String f);
    void addVariable(String varName);
}
