package compiler;

public class ParserException extends Exception {
    public ParserException(String message, int row, int column) {
        super(String.format("%s at row = %d, column = %d", message, row, column));
    }

    public ParserException() {
        super();
    }

    public ParserException(String message) {
        super(message);
    }
}
