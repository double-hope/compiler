package compiler;

import java.util.HashMap;
import java.util.List;

public interface INamespace {

    FuncStatement getFuncByName(String f);
    boolean thereIsFuncWithName(String f);
    void addVariable(String varName);
}
