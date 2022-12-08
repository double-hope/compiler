package compiler;

import java.util.*;

public class AsmCodeGenerator {
    private final String moduleName = "name";
    private final AstTree root;
    private Namespace currentNameSpace;
    private int currentFreeId;
    private final List<String> functions;
    private final List<String> functionProtoNames;
    public String asmCode;

    private static final HashMap<Class, String> statementCodeMap;
    static {
        statementCodeMap = new HashMap<>();
        statementCodeMap.put(PrintStatement.class, Constants.PRINT_STATEMENT_ASM);
        statementCodeMap.put(AssignStatement.class, Constants.ASSIGN_STATEMENT_ASM);
        statementCodeMap.put(ExpressionStatement.class, Constants.EXPRESSION_STATEMENT_ASM);
        statementCodeMap.put(IfStatement.class, Constants.IF_STATEMENT_ASM);
        statementCodeMap.put(ElseStatement.class, Constants.ELSE_STATEMENT_ASM);
        statementCodeMap.put(WhileLoopStatement.class, Constants.WHILE_STATEMENT_ASM);
    }

    private final List<String> statementsList;
    private final String ProcedureTemplate = Constants.PROCEDURE_ASM;
    private final String ProtoTemplate = Constants.PROTO_ASM;
    private final String MasmCodeTemplate = Constants.MASM_CODE_TEMPLATE;

    public AsmCodeGenerator(AstTree root) {
        functionProtoNames = new ArrayList<>();
        statementsList = new ArrayList<>();
        functions = new ArrayList<>();
        this.root = root;
        this.currentNameSpace = root;
    }

    public void generateAsm() throws AsmGeneratorException {
        for (Ast child: this.root.root.getChildren()) {
            statementsList.add(generateCode(child));
        }


        asmCode = String.format(MasmCodeTemplate, String.join("", functionProtoNames),
                String.join("", statementsList),
                String.join("", functions),
                (currentNameSpace.variables.size() * 4));
    }

    private String generateFunction(FuncStatement funcStatement) throws AsmGeneratorException {
        Namespace oldNameSpace = currentNameSpace;
        currentNameSpace = funcStatement;
        StringBuilder bodyStatements = new StringBuilder();
        bodyStatements.append(String.format(Constants.VARIABLE_ASM, funcStatement.varCounter * 4));

        for (Ast statement : funcStatement.getChildren()) {
            bodyStatements.append(generateCode(statement));
            bodyStatements.append('\n');
        }

        bodyStatements.append(String.format(Constants.FUNCTION_BODY_PARAMS_ASM, funcStatement.varCounter * 4));

        bodyStatements.append(String.format(Constants.FUNCTION_BODY_ARGUMENTS_ASM, funcStatement.args.size() * 4));

        currentNameSpace = oldNameSpace;
        functionProtoNames.add(String.format(ProtoTemplate, funcStatement.name));

        functions.add(String.format(ProcedureTemplate, funcStatement.name, bodyStatements));
        return "\n";
    }

    private String generateWhileLoop(WhileLoopStatement whileLoopStatement) throws AsmGeneratorException {
        String id = generateId();
        return String.format(statementCodeMap.get(whileLoopStatement.getClass()), id,
                generateExpr(whileLoopStatement.condition),
                generateCode(whileLoopStatement.getChildren().get(0)));
    }

    private String generateBinExpr(BinaryOperationExpression expression) throws AsmGeneratorException {
        String left = generateExpr(expression.left);
        String right = generateExpr(expression.right);

        return switch (expression.operation){
            case Sum -> String.format(Constants.SUM_ASM, right, left);
            case Subtract -> String.format(Constants.SUBSTRACT_ASM, right, left);
            case Multiply -> String.format(Constants.MULTIPLY_ASM, right, left);
            case Divide -> String.format(Constants.DIVIDE_ASM, right, left);
            case Equal -> String.format(Constants.EQUAL_ASM, right, left);
            case NotEqual -> String.format(Constants.NOT_EQUAL_ASM, right, left);
            case Greater -> String.format(Constants.GREATER_ASM, right, left);
            case Modulo -> String.format(Constants.MODULO_ASM, right, left);
            default -> throw new AsmGeneratorException(String.format("%s not implemented yet", expression.operation));
        };
    }

    private String generateConstExpr(ConstExpression expression) {
        return String.format(Constants.CONST_ASM, (Object) expression.value);
    }

    private String generateVarExpr(VarExpression expression) {
        return String.format(Constants.VAR_EXPRESSION_ASM, getVarOffset(expression.varName), expression.varName);
    }

    private String generateReturn(Expression returnExpression) throws AsmGeneratorException {
        return String.format(Constants.RETURN_ASM, generateExpr(returnExpression),
                ((FuncStatement) currentNameSpace).varCounter * 4, ((FuncStatement) currentNameSpace).args.size() * 4);
    }

    private StringBuilder generateCallExpression(CallExpression callExpression) throws AsmGeneratorException {
        StringBuilder stringBuilder = new StringBuilder();
        Collections.reverse(callExpression.args);
        if (callExpression.args.size() > 0) {
            for (int i = 0; i < callExpression.args.size(); i++) {
                Expression arg = callExpression.args.get(i);
                stringBuilder.append(generateExpr(arg));
            }
        }

        stringBuilder.append(String.format(Constants.CALL_EXPRESSION_ASM, callExpression.name));
        return stringBuilder;
    }

    private String generateConditionalExpression(ConditionalExpression conditionalExpression) throws AsmGeneratorException {
        String currId = generateId();
        if (conditionalExpression.elseBody != null) {
            return String.format("%s%s%s%s",
                    String.format(Constants.CONDITION_IF_WITH_ELSE_ASM, generateExpr(conditionalExpression.condition), currId),
                    String.format(Constants.CONDITION_BODY_ASM, generateExpr(conditionalExpression.body), currId),
                    String.format(Constants.CONDITION_ELSE_ASM, currId, generateExpr(conditionalExpression.elseBody)),
                    String.format(Constants.ID_ASM, currId));
        }

        return String.format(Constants.CONDITION_IF_ASM, generateExpr(conditionalExpression.condition), currId) +
                String.format("%s\n", generateExpr(conditionalExpression.body))  +
                String.format(Constants.ID_ASM, currId);
    }

    private String generateExpr(Expression expression) throws AsmGeneratorException {
        return switch (expression) {
            case BinaryOperationExpression binaryOperationExpression -> generateBinExpr(binaryOperationExpression);
            case ConstExpression constExpression -> generateConstExpr(constExpression);
            case VarExpression varExpression -> generateVarExpr(varExpression);
            case CallExpression callExpression -> generateCallExpression(callExpression);
            case ConditionalExpression conditionalExpression -> generateConditionalExpression(conditionalExpression);
            default -> throw new AsmGeneratorException(
                    String.format("%s at row = %d column = %d", expression.getClass().toString(), expression.row, expression.column));
        };
    }

    private String generateId() {
        return "{ModuleName}{_currentFreeId++}";
    }

    private String getVarOffset(String varName) {
        return currentNameSpace.variables.get(varName) < 0
                ? String.format("+%d", -currentNameSpace.variables.get(varName))
            : String.format("-%d", currentNameSpace.variables.get(varName));
    }

    private String trimPush(String s) -> s.EndsWith("push eax\n") ? s[..s.IndexOf("push eax\n", StringComparison.Ordinal)] : s;

    private String generateCode(Ast st) throws AsmGeneratorException {

        return switch (st) {
            case AssignStatement assignStatement -> generateAssigStatement(assignStatement);
            case BlockStatement blockStatement -> generateBlockStatement(blockStatement);
            case WhileLoopStatement whileLoop -> generateWhileLoop(whileLoop);
            case ExpressionStatement expressionStatement -> generateExpressionStatement(expressionStatement);
            case ElseStatement elseStatement -> generateElseStatement(elseStatement);
            case IfStatement ifStatement -> generateIfStatement(ifStatement);
            case FuncStatement funcStatement -> generateFunction(funcStatement);
            case ReturnStatement returnStatement -> generateReturn(returnStatement._return);
            case PrintStatement print -> String.format(statementCodeMap.get(print.getClass()),
                    trimPush(generateExpr(print.expression)));
            default -> throw new AsmGeneratorException(
                   String.format("Unknown type: %s %d:%d", st.getClass().toString(), st.row + 1, st.column + 1));
        };
    }

    private String generateIfStatement(IfStatement ifStatement) throws AsmGeneratorException {
        return String.format(statementCodeMap.get(ifStatement.getClass()),
                generateExpr(ifStatement.condition),
                generateId(),
                generateCode(ifStatement.getChildren().get(0)));
    }

    private String generateElseStatement(ElseStatement elseStatement) throws AsmGeneratorException {
        return String.format(statementCodeMap.get(elseStatement.getClass()),
                generateExpr(elseStatement.condition),
                generateId(),
                generateCode(elseStatement.getChildren().get(0)),
                generateCode(elseStatement.getChildren().get(1))
        );
    }

    private String generateExpressionStatement(ExpressionStatement exprStatement) throws AsmGeneratorException {
        return String.format(statementCodeMap.get(exprStatement.getClass()),
                trimPush(generateExpr(exprStatement.expression)));
    }

    private String generateBlockStatement(BlockStatement blockStatement) {
        return String.join('\n',
                blockStatement.getChildren()
                        .Select(c => GenerateCode(c) + '\n'));
    }

    private String generateAssigStatement(AssignStatement assignStatement) throws AsmGeneratorException {
        return String.format(statementCodeMap.get(assignStatement.getClass()),
                generateExpr(assignStatement.expression),
                getVarOffset(assignStatement.varName));
    }
}
