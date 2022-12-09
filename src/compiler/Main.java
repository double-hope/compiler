package compiler;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static List<Token> lexing(String code) throws LexerException{
        String codeWithSpaces = code.replace("\t", Constants.FOUR_SPACES);
        Lexer lexer = new Lexer(codeWithSpaces);
        lexer.tokenize();
        return lexer.tokens;
    }

    private static AstTree parsing(List<Token> tokens) throws ParserException {
        Parser parser = new Parser(tokens);
        parser.parse();
        return parser.astTree;
    }

    private static String asmCodeGenerator(AstTree astTree) throws AsmGeneratorException {
        AsmCodeGenerator asmCodeGenerator = new AsmCodeGenerator(astTree);
        asmCodeGenerator.generateAsm();
        return asmCodeGenerator.asmCode;
    }

    private static void writeCodeToFile(String asmCode) throws IOException {
        FileOutputStream fs = (FileOutputStream) Files.createFile(Path.of(System.getProperty("user.dir").concat(Constants.OUTPUT_FILE_NAME)));
        byte[] bytes = asmCode.getBytes(StandardCharsets.UTF_8);
        fs.write(bytes, 0, bytes.length);
    }

    public static void main(String[] args) throws IOException, LexerException, AsmGeneratorException, ParserException {
        String pyText = Files.readString(Path.of(System.getProperty("user.dir").concat(Constants.INPUT_FILE_NAME)));
        List<Token> tokens = lexing(pyText);
        AstTree astrTree = parsing(tokens);
        String asmCode = asmCodeGenerator(astrTree);
        writeCodeToFile(asmCode);
    }
}
