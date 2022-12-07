package compiler;

import java.util.ArrayList;
import java.util.List;
public class Lexer {
    private final String _code;

    public List<Token> Tokens = new ArrayList<>();

    private int _currentLevel;

    public Lexer(String code) {
        _code = code;
        _currentLevel = 0;
    }

    public void Tokenize() throws LexerException{
        String[] strings = _code.split(System.lineSeparator());
        for (var i = 0; i < strings.length; i++) {
            if (ParseLine(strings[i], i)) {
                Tokens.add(new Token(TokenType.Newline, "\n", i, strings[i].length()));
            }
        }
    }

    private static int CountLevel(String str) {
        var spaces = 0;
        char[] chars = str.toCharArray();

        for (char chr : chars) {
            if (chr == ' ') {
                spaces++;
            }
            else {
                break;
            }
        }

        return spaces / 4;
    }

    private boolean ParseLine(String line, int row) throws LexerException{

        char[] chars = line.toCharArray();

        for (char chr: chars) {
            if(chr == '\t') return false;
        }

        if (line.length() != 0 && line.charAt(0) == '#') {
            return false;
        }

        var newCurrentLevel = CountLevel(line);
        if (newCurrentLevel - _currentLevel > 1) {
            throw new LexerException("Not expected indent at {row + 1}");
        } else if (newCurrentLevel - _currentLevel == 1) {
            _currentLevel = newCurrentLevel;
            Tokens.add(new Token(TokenType.Indent, "\t", row, 0));
        } else {
            if (_currentLevel - newCurrentLevel > 0) {
                var difference = _currentLevel - newCurrentLevel;
                while (difference > 0) {
                    difference--;
                    Tokens.add(new Token(TokenType.Dedent, null, row, difference));
                }

                _currentLevel = newCurrentLevel;
            }
        }

        var pos = newCurrentLevel;

        while (pos < line.length()) {
            switch (line.charAt(pos)) {
                case ' ':
                    pos++;
                    break;
                case '#':
                    return true;
                default: {
                    if (Character.isDigit(line.charAt(pos))) {
                        pos += StartsWithDigit(line, row, pos);
                    }
                    else if (Character.isLetter(line.charAt(pos))) {
                        pos += StartsWithLetter(line, row, pos);
                    }
                    else if (Constants.SYMBOLS.contains(line.charAt(pos))) {
                        pos += StartsWithSym(line, row, pos);
                    }

                    break;
                }
            }
        }

        return true;
    }

    private boolean tryParseInt(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    private int StartsWithDigit(String str, int row, int col) throws LexerException{
        var pos = col;
        var st = new StringBuilder(str.length() - col);
        while (pos < str.length()) {
            if (Character.isDigit(str.charAt(pos))) st.append(str.charAt(pos));
            else break;
            pos++;
        }

        if (!tryParseInt(st.toString()))
            throw new LexerException("invalid syntax at {str} {row + 1}:{col}");
        Tokens.add(new Token(TokenType.IntegerNumber, st.toString(), row, col));
        return st.length();
    }

    private int LexerChars(String str, int row, int col, StringBuilder st) {
        if (st.length() >= 2) {
            if (LexerTwoChars(str.charAt(col), str.charAt(col + 1)) != TokenType.NotImplemented) {
                Tokens.add(new Token(LexerTwoChars(str.charAt(col), str.charAt(col + 1)),
                        LexerTwoChars(str.charAt(col), str.charAt(col + 1)).toString(), row, col));
                return 2;
            }
        }

        if (LexerSingleChar(str.charAt(col)) == TokenType.NotImplemented) return 0;
        Tokens.add(new Token(LexerSingleChar(str.charAt(col)), LexerSingleChar(str.charAt(col)).toString(), row, col));
        return 1;
    }

    private int StartsWithLetter(String str, int row, int column) {
        var st = new StringBuilder(str.length() - column);
        var pos = column;
        while (pos < str.length() && (Character.isDigit(str.charAt(pos)) || Character.isLetter(str.charAt(pos)))) {
            st.append(str.charAt(pos));
            pos++;
        }

        switch (st.toString()) {
            case Constants.PYTHON_FUNCTION_DEFENITION ->
                    Tokens.add(new Token(TokenType.FuncDefinition, st.toString(), row, column));
            case Constants.PYTHON_WHILE -> Tokens.add(new Token(TokenType.WhileLoop, st.toString(), row, column));
            case Constants.PYTHON_PRINT -> Tokens.add(new Token(TokenType.PrintOperator, st.toString(), row, column));
            case Constants.PYTHON_RETURN -> Tokens.add(new Token(TokenType.Return, st.toString(), row, column));
            case Constants.PYTHON_IF -> Tokens.add(new Token(TokenType.IfCondition, st.toString(), row, column));
            case Constants.PYTHON_ELSE -> Tokens.add(new Token(TokenType.ElseCondition, st.toString(), row, column));
            default -> Tokens.add(new Token(TokenType.Identifier, st.toString(), row, column));
        }

        return st.toString().length();
    }

    private int StartsWithSym(String str, int row, int col) throws LexerException{
        var st = new StringBuilder(str.length() - col);
        var pos = col;
        while (pos < str.length()) {
            if (Constants.SYMBOLS.contains(str.charAt(pos))) {
                st.append(str.charAt(pos));
                pos++;
            }
            else {
                break;
            }
        }

        if (st.length() > 0) {
            return LexerChars(str, row, col, st);
        }

        throw new LexerException("Unexpected token {str[col]} at {row + 1}:{col}");
    }

    private TokenType LexerTwoChars(int symb1, int symb2) {
        if (symb1 == ('!') && symb2 == ('=')) {
            return TokenType.NotEqual;
        }
        else if(symb1 == ('=') && symb2 == ('='))
        {
            return TokenType.Equal;
        }

        return TokenType.NotImplemented;
    }


    private TokenType LexerSingleChar(char symb) {
        TokenType type;
        switch (symb) {
            case '(' -> type = TokenType.OpenBracket;
            case ')' -> type = TokenType.CloseBracket;
            case '*' -> type = TokenType.Multiply;
            case ',' -> type = TokenType.Comma;
            case '-' -> type = TokenType.Subtract;
            case '+' -> type = TokenType.Sum;
            case '/' -> type = TokenType.Divide;
            case ':' -> type = TokenType.Colon;
            case '=' -> type = TokenType.Assignment;
            case '>' -> type = TokenType.Greater;
            case '%' -> type = TokenType.Modulo;
            default -> type = TokenType.NotImplemented;
        }

        return type;
    }
}
