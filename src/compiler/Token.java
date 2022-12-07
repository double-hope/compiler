package compiler;

public class Token {
    public TokenType type;

    public String data;
    public int row;

    public int column;

    public Token(TokenType type, String data, int row, int column) {
        this.type = type;
        this.data = data;
        this.row = row;
        this.column = column;
    }
}