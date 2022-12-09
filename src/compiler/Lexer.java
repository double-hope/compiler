package compiler;

import java.util.ArrayList;
import java.util.List;
public class Lexer {
    private final String _code;

    public List<Token> tokens = new ArrayList<>();

    private int _currentLevel;

    public Lexer(String code) {
        _code = code;
        _currentLevel = 0;
    }

    public void tokenize() throws LexerException{
        String[] strings = _code.split(System.lineSeparator());
        for (int i = 0; i < strings.length; i++) {
            if (parseLine(strings[i], i)) {
                tokens.add(new Token(TokenType.Newline, "\n", i, strings[i].length()));
            }
        }
    }

    private static int countLevel(String str) {
        int spaces = 0;
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

    private boolean parseLine(String line, int row) throws LexerException{

        char[] chars = line.toCharArray();

        for (char chr: chars) {
            if(chr == '\t') return false;
        }

        if (line.length() != 0 && line.charAt(0) == '#') {
            return false;
        }

        int newCurrentLevel = countLevel(line);
        if (newCurrentLevel - _currentLevel > 1) {
            throw new LexerException(String.format("Not expected indent at %d", row + 1));
        } else if (newCurrentLevel - _currentLevel == 1) {
            _currentLevel = newCurrentLevel;
            tokens.add(new Token(TokenType.Indent, "\t", row, 0));
        } else {
            if (_currentLevel - newCurrentLevel > 0) {
                int difference = _currentLevel - newCurrentLevel;
                while (difference > 0) {
                    difference--;
                    tokens.add(new Token(TokenType.Dedent, null, row, difference));
                }

                _currentLevel = newCurrentLevel;
            }
        }

        int pos = newCurrentLevel;

        while (pos < line.length()) {
            switch (line.charAt(pos)) {
                case ' ':
                    pos++;
                    break;
                case '#':
                    return true;
                default: {
                    if (Character.isDigit(line.charAt(pos))) {
                        pos += startsWithDigit(line, row, pos);
                    }
                    else if (Character.isLetter(line.charAt(pos))) {
                        pos += startsWithLetter(line, row, pos);
                    }
                    else if (Constants.SYMBOLS.contains(line.charAt(pos))) {
                        pos += startsWithSym(line, row, pos);
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
    private int startsWithDigit(String str, int row, int col) throws LexerException{
        int pos = col;
        StringBuilder st = new StringBuilder(str.length() - col);
        while (pos < str.length()) {
            if (Character.isDigit(str.charAt(pos))) st.append(str.charAt(pos));
            else break;
            pos++;
        }

        if (!tryParseInt(st.toString()))
            throw new LexerException(String.format("invalid syntax at %s %d:%d", str, row + 1, col) );
        tokens.add(new Token(TokenType.IntegerNumber, st.toString(), row, col));
        return st.length();
    }

    private int lexerChars(String str, int row, int col, StringBuilder st) {
        if (st.length() >= 2) {
            if (lexerTwoChars(str.charAt(col), str.charAt(col + 1)) != TokenType.NotImplemented) {
                tokens.add(new Token(lexerTwoChars(str.charAt(col), str.charAt(col + 1)),
                        lexerTwoChars(str.charAt(col), str.charAt(col + 1)).toString(), row, col));
                return 2;
            }
        }

        if (lexerSingleChar(str.charAt(col)) == TokenType.NotImplemented) return 0;
        tokens.add(new Token(lexerSingleChar(str.charAt(col)), lexerSingleChar(str.charAt(col)).toString(), row, col));
        return 1;
    }

    private int startsWithLetter(String str, int row, int column) {
        StringBuilder st = new StringBuilder(str.length() - column);
        int pos = column;
        while (pos < str.length() && (Character.isDigit(str.charAt(pos)) || Character.isLetter(str.charAt(pos)))) {
            st.append(str.charAt(pos));
            pos++;
        }

        switch (st.toString()) {
            case Constants.PYTHON_FUNCTION_DEFINITION ->
                    tokens.add(new Token(TokenType.FuncDefinition, st.toString(), row, column));
            case Constants.PYTHON_WHILE -> tokens.add(new Token(TokenType.WhileLoop, st.toString(), row, column));
            case Constants.PYTHON_PRINT -> tokens.add(new Token(TokenType.PrintOperator, st.toString(), row, column));
            case Constants.PYTHON_RETURN -> tokens.add(new Token(TokenType.Return, st.toString(), row, column));
            case Constants.PYTHON_IF -> tokens.add(new Token(TokenType.IfCondition, st.toString(), row, column));
            case Constants.PYTHON_ELSE -> tokens.add(new Token(TokenType.ElseCondition, st.toString(), row, column));
            default -> tokens.add(new Token(TokenType.Identifier, st.toString(), row, column));
        }

        return st.toString().length();
    }

    private int startsWithSym(String str, int row, int col) throws LexerException{
        StringBuilder st = new StringBuilder(str.length() - col);
        int pos = col;
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
            return lexerChars(str, row, col, st);
        }

        throw new LexerException(String.format("Unexpected token %s at %d:%d", str.charAt(col), row + 1, col));
    }

    private TokenType lexerTwoChars(int symbol1, int symbol2) {
        if (symbol1 == ('!') && symbol2 == ('=')) {
            return TokenType.NotEqual;
        }
        else if(symbol1 == ('=') && symbol2 == ('='))
        {
            return TokenType.Equal;
        }

        return TokenType.NotImplemented;
    }


    private TokenType lexerSingleChar(char symbol) {
        TokenType type;
        switch (symbol) {
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
