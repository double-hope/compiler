package compiler;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    private static List<Token> lexing(String code) throws LexerException{
        String codeWithSpaces = code.replace("\t", Constants.FOUR_SPACES);
        Lexer lexer = new Lexer(codeWithSpaces);
        lexer.Tokenize();
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

    private static void writeCodeToFile(String asmCode) {
        using var fs = File.Create(Directory.GetParent(
                Directory.GetCurrentDirectory()) + Constants.OUTPUT_FILE_NAME);
        var bytes = new UTF8Encoding(true).GetBytes(asmCode);
        fs.Write(bytes, 0, bytes.Length);
    }

    public static void main(String[] args) throws IOException, LexerException, AsmGeneratorException, ParserException {

        String pyText = Files.readString(Path.of(Constants.BASE_PATH.concat(Constants.INPUT_FILE_NAME)));
        List<Token> tokens = lexing(pyText);
        AstTree astrTree = parsing(tokens);
        String asmCode = asmCodeGenerator(astrTree);
        writeCodeToFile(asmCode);
    }
}
