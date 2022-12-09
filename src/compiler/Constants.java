package compiler;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public static final String FOUR_SPACES = "    ";
    public static final String INPUT_FILE_NAME = "\\KP-7-Java-IO-03-Prokhorchuk.py";
    public static final String OUTPUT_FILE_NAME = "\\KP-7-Java-IO-03-Prokhorchuk.asm";
    public static final List<Character> SYMBOLS = Arrays.asList('(', ')', '*', ',', '-', '+', '/', ':', '=', '>', '!', '%');
    public static final String MASM_CODE_TEMPLATE = """
            .386
            .model flat,stdcall
            option casemap:none

            include \\masm32\\include\\masm32rt.inc
            _main PROTO

            %s
            .data
            .code
            _start:
            push ebp
            mov ebp, esp
            sub esp, %d
            invoke  _main
            add esp, %d
            mov esp, ebp
            pop ebp
            ret
            _main PROC

            %s
            printf("\\n")
            inkey
            ret

            _main ENDP

            %sEND _start

            """;
    public static final String PROTO_ASM = "%s PROTO\n";
    public static final String ASSIGN_STATEMENT_ASM =
            "%s\n\tpop eax\n\tmov dword ptr[ebp%s], eax\n";
    public static final String IF_STATEMENT_ASM = """
            %spop eax
            cmp eax, 0
            je %selse
            %s%selse:
            """;
    public static final String ELSE_STATEMENT_ASM = """
            %spop eax
            cmp eax, 0
            je %selse
            %sjmp %sfinal
            %selse:
            %s%sfinal:
            """;

    public static final String PRINT_STATEMENT_ASM =
            "%sfn MessageBoxA,0, str$(eax), \"Prokhorchuk Nadiia IO-03\", MB_OK\n";
    public static final String WHILE_STATEMENT_ASM = """
            Loop%sstart:
            %spop eax
            cmp eax, 0
            je Loop%send
            %sjmp Loop%sstart
            Loop%send:
            """;
    public static final String EXPRESSION_STATEMENT_ASM = "%s\n";
    public static final String PROCEDURE_ASM = """
            %s PROC
            %s
            %s ENDP
            """;
    public static final String FUNCTION_BODY_ARGUMENTS_ASM = "ret %d\n";
    public static final String VAR_EXPRESSION_ASM = "mov eax, dword ptr[ebp%s] ; %s\npush eax\n";
    public static final String SUM_ASM = "%s\n%s\npop eax\npop ecx\nadd eax, ecx\npush eax\n";
    public static final String SUBTRACT_ASM = "%s\n%s\npop eax\npop ecx\nsub eax, ecx\npush eax\n";
    public static final String DIVIDE_ASM = "%s\n%s\npop eax\npop ebx\nxor edx, edx\ndiv ebx\npush eax\n";
    public static final String MULTIPLY_ASM = "%s\n%s\npop eax\npop ecx\nimul ecx\npush eax\n";
    public static final String MODULO_ASM = "%s\n%s\npop eax\npop ebx\nxor edx, edx\ndiv ebx\npush edx\n";
    public static final String CALL_EXPRESSION_ASM = "invoke %s\n push eax\n";
    public static final String EQUAL_ASM =
            "%s\n%s\npop eax\npop ecx\ncmp eax, ecx\nmov eax, 0\nsete al\npush eax\n";
    public static final String NOT_EQUAL_ASM =
            "%s\n%s\npop eax\npop ecx\ncmp eax, ecx\nmov eax, 0\nsetne al\npush eax\n";
    public static final String GREATER_ASM = "%s\n%s\npop eax\npop ecx\ncmp ecx, eax\nmov eax, 0\nsetl al\npush eax\n";
    public static final String CONDITION_BODY_ASM = "%s\njmp %sfinal\n";
    public static final String CONDITION_ELSE_ASM = "%selse:\n%s\n";
    public static final String VARIABLE_ASM = "push ebp\nmov ebp, esp\nsub esp, %d\n";
    public static final String FUNCTION_BODY_PARAMS_ASM = "add esp, %d\nmov esp, ebp\npop ebp\n";
    public static final String CONST_ASM = "\tpush %s\n";
    public static final String RETURN_ASM = "%s\npop eax\nadd esp, %d\nmov esp, ebp\npop ebp\nret %d\n";
    public static final String CONDITION_IF_ASM = "%s\npop eax\ncmp eax, 0\nje %sfinal\n";
    public static final String CONDITION_IF_WITH_ELSE_ASM = "%s\npop eax\ncmp eax, 0\nje %selse\n";
    public static final String ID_ASM = "%sfinal:\n";
    public static final String PYTHON_PRINT = "print";
    public static final String PYTHON_WHILE = "while";
    public static final String PYTHON_IF = "if";
    public static final String PYTHON_ELSE = "else";
    public static final String PYTHON_RETURN = "return";
    public static final String PYTHON_FUNCTION_DEFINITION = "def";
}