package compiler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {
    private CustomIterator<Token> tokensIterator;
    public AstTree astTree;

    private Namespace currentNameSpace;

    public Parser(List<Token> tokens) {
        this.astTree = new AstTree();
        this.tokensIterator = new CustomIterator<Token>(tokens.GetEnumerator());
        this.currentNameSpace = astTree;
    }

    public void parse() throws ParserException {
        startParse(astTree.root);
    }

    private Statement parseWhileLoop() throws ParserException {
        var token = tokensIterator.getCurrent();
        tokensIterator.moveNext();
        var ret = new WhileLoopStatement(token.row,
                token.column, parseExpr());
        sameCurrent(TokenType.Colon);
        if (!tokensIterator.hasNext()) {
            tokensIterator.movePrev();
            if (tokensIterator.getCurrent() != null)
                throw new ParserException(
                        "Expected token {tokensIterator.getCurrent()} at {tokensIterator.getCurrent().row}:{tokensIterator.getCurrent().column}");
        }

        if (tokensIterator.getCurrent() == null) return ret;
        var body = new BlockStatement(tokensIterator.getCurrent().row, tokensIterator.getCurrent().column);
        if (sameToCurrentBool(TokenType.Newline)) {
            same(TokenType.Indent);
            tokensIterator.movePrev();
            startParse(body, TokenType.Dedent);
        }
        else {
            startParse(body, TokenType.Newline);
        }

        ret.addChild(body);

        return ret;
    }

    private Statement parseConditional() throws ParserException {
        RowCol rowCol = new RowCol(tokensIterator.getCurrent().row, tokensIterator.getCurrent().column);
        
        if (!tokensIterator.moveNext()) {
            throw new ParserException("Token expected",
                    rowCol.row, rowCol.column);
        }

        var condition = parseExpr();
        sameCurrent(TokenType.Colon);

        var body = new BlockStatement(tokensIterator.getCurrent().row,
                tokensIterator.getCurrent().column);

        if (!tokensIterator.moveNext())
            if (tokensIterator.getCurrent() != null)
                throw new ParserException("Token expected",
                        tokensIterator.getCurrent().row, tokensIterator.getCurrent().column);

        same(TokenType.Indent);
        tokensIterator.movePrev();
        startParse(body, (tokensIterator.getCurrent().type == TokenType.Newline) ? TokenType.Dedent : TokenType.Newline);

        if (sameBool(TokenType.ElseCondition)) {
            var conditionalElseStatement = new ElseStatement(rowCol.row,
                    rowCol.column,
                    condition
            );
            if (tokensIterator.getCurrent() == null) return conditionalElseStatement;
            var elseBody = new BlockStatement(tokensIterator.getCurrent().row,
                    tokensIterator.getCurrent().column);
            if (!tokensIterator.moveNext())
                if (tokensIterator.getCurrent() != null)
                    throw new ParserException("Token expected",
                            tokensIterator.getCurrent().row, tokensIterator.getCurrent().column);
            tokensIterator.moveNext();
            same(TokenType.Indent);
            tokensIterator.movePrev();
            startParse(elseBody, (tokensIterator.getCurrent().type == TokenType.Newline) ? TokenType.Dedent : TokenType.Newline);
            conditionalElseStatement.addChild(body);
            conditionalElseStatement.addChild(elseBody);

            return conditionalElseStatement;
        }

        var conditionalStatement = new IfStatement(rowCol.row,
                rowCol.column,
                condition
        );
        conditionalStatement.addChild(body);

        return conditionalStatement;
    }

    private List<Expression> checkArguments() throws ParserException {
        List<Expression> res = new ArrayList<>();
        same(TokenType.OpenBracket);
        while (tokensIterator.moveNext()) {
            if (tokensIterator.getCurrent() != null)
                switch (tokensIterator.getCurrent().type) {
                    case Identifier:
                    case IntegerNumber:
                        res.add(parseExpr());
                        switch (tokensIterator.getCurrent().type) {
                            case Comma:
                                break;
                            case CloseBracket:
                                return res;
                            default:
                                throw new ParserException(
                                        "Unexpected token {tokensIterator.getCurrent().type.ToString()} at {tokensIterator.getCurrent().row + 1}:{tokensIterator.getCurrent().column}",
                                        tokensIterator.getCurrent().row, tokensIterator.getCurrent().column
                                );
                        }

                        break;
                    case CloseBracket:
                        return res;
                    default:
                        throw new ParserException(
                                "Unexpected token {tokensIterator.getCurrent().type.ToString()} at {tokensIterator.getCurrent().row + 1}:{tokensIterator.getCurrent().column}",
                                tokensIterator.getCurrent().row, tokensIterator.getCurrent().column
                        );
                }
        }

        return res;
    }

    private FuncStatement parseFunc(HashMap<String, Integer> varTable) throws ParserException {
        var def = new FuncStatement(tokensIterator.getCurrent().row, tokensIterator.getCurrent().column, varTable) {
            name = same(TokenType.Identifier).data;
            args = MatchDefArgs();
        };
        def.funcList = new ArrayList<>(currentNameSpace.funcList);
        currentNameSpace.funcList.add(def);
        for (String arg: def.args) {
            def.variables.remove(arg);

            def.addArg(arg);
        }

        same(TokenType.Colon);

        if (sameBool(TokenType.Newline)) {
            var prevNameSpace = currentNameSpace;
            currentNameSpace = def;
            startParse(def, TokenType.Dedent);
            currentNameSpace = prevNameSpace;
        }
        else {
            sameCurrent(TokenType.Return);
            def._return = parseExpr();
            sameCurrent(TokenType.Newline);
        }

        return def;
    }

    private Expression parseExpr() throws ParserException {
        var first = parseTerm();
        while (sameToCurrentBool(TokenType.Sum))
        {
            if (tokensIterator.getCurrent() == null) continue;
            var operatorType = tokensIterator.getCurrent().type;
            if (!tokensIterator.moveNext()) continue;
            var second = parseTerm();
            first = new BinaryOperationExpression(first.row,
                    first.column,
                    operatorType,
                    first,
                    second
            );
        }

        while (sameToCurrentBool(TokenType.Subtract)) {
            if (tokensIterator.getCurrent() == null) continue;
            var operatorType = tokensIterator.getCurrent().type;
            if (!tokensIterator.moveNext()) continue;
            var second = parseTerm();
            first = new BinaryOperationExpression(first.row,
                    first.column,
                    operatorType,
                    first,
                    second
            );
        }

        if (sameToCurrentBool(TokenType.IfCondition) && tokensIterator.moveNext()) {
            var condition = parseExpr();

            sameCurrent(TokenType.ElseCondition);
            tokensIterator.moveNext();
            var elseExpression = parseExpr();
            return new ConditionalExpression(first.row,
                    first.column,
                    first,
                    condition,
                    elseExpression);
        }

        if (!sameToCurrentBool(TokenType.Greater)) return first;
        if (tokensIterator.getCurrent() == null) return first;
        var op = tokensIterator.getCurrent().type;
        if (!tokensIterator.moveNext()) return first;
        var third = parseExpr();
        first = new BinaryOperationExpression(first.row,
                first.column,
                op,
                first,
                third
        );

        return first;
    }

    private Expression parseTerm() throws ParserException {
        var first = parseFactor();
        while (tokensIterator.moveNext() &&
                (sameToCurrentBool(TokenType.Multiply) ||
                        sameToCurrentBool(TokenType.Divide) ||
                        sameToCurrentBool(TokenType.Equal) ||
                        sameToCurrentBool(TokenType.NotEqual) ||
                        sameToCurrentBool(TokenType.Subtract) ||
                        sameToCurrentBool(TokenType.Sum) ||
                        sameToCurrentBool(TokenType.Modulo))) {
            if (tokensIterator.getCurrent() == null) continue;
            var termOperator = tokensIterator.getCurrent().type;
            var errorrow = tokensIterator.getCurrent().row;
            var errorCol = tokensIterator.getCurrent().column;
            if (tokensIterator.moveNext()) {
                Expression second = parseFactor();
                first = new BinaryOperationExpression(first.row, first.column, termOperator, first, second);
            }
            else {
                throw new ParserException("Expected token", errorrow, errorCol);
            }
        }


        return first;
    }

    private Expression parseFactor() throws ParserException {
        if (sameToCurrentBool(TokenType.OpenBracket)) {
            if (tokensIterator.moveNext()) {
                var expr = parseExpr();
                sameCurrent(TokenType.CloseBracket);
                return expr;
            }
        }

        if (sameToCurrentBool(TokenType.IntegerNumber)) {
            if (tokensIterator.getCurrent() != null)
                return new ConstExpression(tokensIterator.getCurrent().row,
                        tokensIterator.getCurrent().column,
                        tokensIterator.getCurrent().data);
        }

        if (!sameToCurrentBool(TokenType.Identifier))
            if (tokensIterator.getCurrent() != null)
                throw new ParserException(
                        "Unexpected token {tokensIterator.getCurrent().type.ToString()} at {tokensIterator.getCurrent().row}:{tokensIterator.getCurrent().column}",
                        tokensIterator.getCurrent().row, tokensIterator.getCurrent().column);
        if (tokensIterator.getCurrent() == null)
            if (tokensIterator.getCurrent() != null)
                throw new ParserException("Variable used before assignment " +
                        "\"{tokensIterator.getCurrent().data.ToString()}\" " +
                        "at {tokensIterator.getCurrent().row}:{tokensIterator.getCurrent().column + 1}");
        var name = tokensIterator.getCurrent().data;
        if (tokensIterator.getCurrent() != null && tokensIterator.moveNext() &&
                tokensIterator.getCurrent().type == TokenType.OpenBracket) {
            tokensIterator.movePrev();
            if (tokensIterator.getCurrent() != null) {
                var ret = new CallExpression(tokensIterator.getCurrent().row,
                        tokensIterator.getCurrent().column,
                        name, checkArguments());
                if (!currentNameSpace.thereIsFuncWithName(ret.name)) {
                    throw new ParserException("Name {ret.Name} is not defined ", ret.row, ret.column);
                }

                if (currentNameSpace.getFuncByName(ret.name).args.size() != ret.args.size()) {
                    throw new ParserException("Function {ret.Name} called with {ret.Args.Count} args, " +
                            "but it have {_currentNameSpace.GetFuncByName(ret.Name).Args.Count} args " +
                            "at {ret.row + 1} : {ret.column + 1}",
                            ret.row, ret.column);
                }

                return ret;
            }
        }

        tokensIterator.movePrev();
        if (tokensIterator.getCurrent() != null &&
                currentNameSpace.variables.containsKey(tokensIterator.getCurrent().data)) {
            return new VarExpression(tokensIterator.getCurrent().row,
                    tokensIterator.getCurrent().column,
                    name);
        }

        throw new ParserException("Variable used before assignment " +
                "\"{tokensIterator.getCurrent().data.ToString()}\" " +
                "at {tokensIterator.getCurrent().row}:{tokensIterator.getCurrent().column + 1}");
    }

    private Token same(TokenType tokenType) throws ParserException {
        if (!tokensIterator.moveNext()) throw new ParserException();
        if (tokensIterator.getCurrent() != null && tokenType != tokensIterator.getCurrent().type) {
            throw new ParserException("Got " + tokensIterator.getCurrent().type +
                    ", {tokenType.ToString()} expected" +
                    " at {tokensIterator.getCurrent().row + 1}:{tokensIterator.getCurrent().column + 1}",
                    tokensIterator.getCurrent().row, tokensIterator.getCurrent().column);
        }

        return tokensIterator.getCurrent();
    }

    private void sameCurrent(TokenType tokenType) throws ParserException {
        if (tokensIterator.getCurrent() != null && tokenType != tokensIterator.getCurrent().type) {
            throw new ParserException("Got " + tokensIterator.getCurrent().type +
                    ", {tokenType.ToString()} expected" +
                    " at {tokensIterator.getCurrent().row + 1}:{tokensIterator.getCurrent().column}");
        }
    }

    private boolean sameBool (TokenType tokenType) {
        return tokensIterator.getCurrent() != null && tokensIterator.moveNext() && tokenType == tokensIterator.getCurrent().type;
    }
    private boolean sameToCurrentBool(TokenType tokenType) {
        return tokensIterator.getCurrent() != null && tokenType == tokensIterator.getCurrent().type;
    }

    private void matchIndentation() throws ParserException {
        if (!tokensIterator.moveNext()) return;
        if (sameToCurrentBool(TokenType.Newline) || sameToCurrentBool(TokenType.Dedent)) return;
        if (tokensIterator.getCurrent() != null) System.out.println(tokensIterator.getCurrent().toString());
        throw new ParserException("Expected new line or semicolon");
    }

    private void matchIndentationCurrent() throws ParserException {
        if (sameToCurrentBool(TokenType.Newline) || sameToCurrentBool(TokenType.Dedent)) return;
        if (tokensIterator.getCurrent() != null) System.out.println(tokensIterator.getCurrent().toString());
        throw new ParserException("Expected new line or semicolon");
    }

    private Expression matchReturn() throws ParserException {
        same(TokenType.Return);
        if (tokensIterator.getCurrent() != null) {
            var errorrow = tokensIterator.getCurrent().row;
            var errorCol = tokensIterator.getCurrent().column;
            if (!tokensIterator.moveNext())
                throw new ParserException("Expected token",
                        errorrow, errorCol);
        }

        return parseExpr();
    }

    private List<String> matchDefArgs() throws ParserException {
        var res = new ArrayList<String>();
        same(TokenType.OpenBracket);
        while (tokensIterator.moveNext()) {
            if (tokensIterator.getCurrent() != null)
                switch (tokensIterator.getCurrent().type) {
                    case Identifier:
                        res.add(tokensIterator.getCurrent().data);
                        tokensIterator.moveNext();
                        if (tokensIterator.getCurrent() != null)
                            switch (tokensIterator.getCurrent().type) {
                                case Comma:
                                    break;
                                case CloseBracket:
                                    return res;
                                default:
                                    throw new ParserException(
                                            "Unexpected token at {tokensIterator.getCurrent().row + 1}:{tokensIterator.getCurrent().column}",
                                            tokensIterator.getCurrent().row, tokensIterator.getCurrent().column
                                    );
                            }

                        break;
                    case CloseBracket:
                        return res;
                    default:
                        throw new ParserException(
                                "Unexpected token at {tokensIterator.getCurrent().row + 1}:{tokensIterator.getCurrent().column}",
                                tokensIterator.getCurrent().row, tokensIterator.getCurrent().column
                        );
                }
        }

        return res;
    }
    private void startParse(Ast ast) throws ParserException {
        parseUntil(ast, null);
    }
    private void startParse(Ast ast, TokenType stopToken) throws ParserException {
        parseUntil(ast, stopToken);
    }
    private void parseUntil(Ast ast, TokenType stopToken) throws ParserException {
        while (tokensIterator.moveNext()) {
            Token token = tokensIterator.getCurrent();
            if (token.type == stopToken) {
                break;
            }

            switch (token.type) {
                case TokenType.IntegerNumber, TokenType.Subtract, TokenType.OpenBracket -> {
                    var temp = new ExpressionStatement(tokensIterator.getCurrent().row,
                            tokensIterator.getCurrent().column, parseExpr());
                    ast.addChild(temp);
                    matchIndentationCurrent();
                }
                case TokenType.IfCondition -> {
                    Statement temp = parseConditional();
                    ast.addChild(temp);
                }
                case TokenType.Identifier -> {
                    if (tokensIterator.moveNext()) {
                        if (tokensIterator.getCurrent() != null &&
                                tokensIterator.getCurrent().type == TokenType.Assignment) {
                            tokensIterator.movePrev();
                            if (tokensIterator.getCurrent() != null) {
                                String name = tokensIterator.getCurrent().data;
                                tokensIterator.moveNext();
                                tokensIterator.moveNext();
                                Expression expr = parseExpr();
                                ast.addChild(new AssignStatement(
                                        tokensIterator.getCurrent().row,
                                        tokensIterator.getCurrent().column,
                                        name, expr));
                                if (ast instanceof Namespace tableContainer) {
                                    tableContainer.addVariable(name);
                                } else {
                                    currentNameSpace.addVariable(name);
                                }
                            }
                        } else if (tokensIterator.getCurrent().type == TokenType.OpenBracket) {
                            tokensIterator.movePrev();
                            var tempEx = parseExpr();
                            var temp = new ExpressionStatement(tempEx.row, tempEx.column, tempEx);
                            ast.addChild(temp);
                            matchIndentation();
                        } else {
                            tokensIterator.movePrev();
                            ast.addChild(new ExpressionStatement(
                                    tokensIterator.getCurrent().row,
                                    tokensIterator.getCurrent().column,
                                    parseExpr()));
                        }
                    }
                }
                case TokenType.PrintOperator -> {
                    var row = tokensIterator.getCurrent().row;
                    var column = tokensIterator.getCurrent().column;
                    same(TokenType.OpenBracket);
                    var temp = new PrintStatement(row, column, parseExpr());
                    tokensIterator.movePrev();
                    sameCurrent(TokenType.CloseBracket);
                    ast.addChild(temp);
                }
                case TokenType.WhileLoop -> {
                    var temp = parseWhileLoop();
                    ast.addChild(temp);
                }
                case TokenType.Return -> {
                    if (currentNameSpace.getClass() != FuncStatement.class) {
                        throw new ParserException("Unexpected return at {tokensIterator.getCurrent().row}:" +
                                "{tokensIterator.getCurrent().column}");
                    }

                    Token currentToken = tokensIterator.getCurrent();
                    tokensIterator.movePrev();
                    FuncStatement currentNameSpace = ((FuncStatement) currentNameSpace);
                    currentNameSpace._return = matchReturn();
                    ast.addChild(new ReturnStatement(currentToken.row, currentToken.column,
                            currentNameSpace._return));
                }
                case TokenType.FuncDefinition -> {
                    Ast temp =  switch (ast) {
                        case Namespace tableContainer -> parseFunc(new HashMap<>(tableContainer.variables));
                        default -> parseFunc(new HashMap<>(astTree.variables));
                    } ;

                    ast.addChild(temp);
                }
                default -> {}
            }
        }
    }
}
