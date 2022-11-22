package compiler;

import java.util.Arrays;
import java.util.List;

public class Constants {
    public final String FOUR_SPACES = "    ";
    public final String INPUT_FILE_NAME = "Nadiia.py";
    public final String OUTPUT_FILE_NAME = "Nadiia.asm";

    public final static List<Character> SYMBOLS = Arrays.asList('(', ')', '*', ',', '-', '+', '/', ':', '=', '>', '!', '%');


    public final String MASM_CODE_TEMPLATE = """
            .386
            .model flat,stdcall
            option casemap:none

            include \\masm32\\include\\masm32rt.inc
            _main PROTO

            {0}
            .data
            .code
            _start:
            push ebp
            mov ebp, esp
            sub esp, {3}
            invoke  _main
            add esp, {3}
            mov esp, ebp
            pop ebp
            ret
            _main PROC

            {1}
            printf("\\n")
            inkey
            ret

            _main ENDP

            {2}END _start

            """;
    public final String PROTO_ASM = "{0} PROTO\n";
    public final String ASSIGN_STATEMENT_ASM =
            "{0}\n\tpop eax\n\tmov dword ptr[ebp{1}], eax\n";
    public final String IF_STATEMENT_ASM = """
            {0}pop eax
            cmp eax, 0
            je {1}else
            {2}{1}else:
            """;
    public final String ELSE_STATEMENT_ASM = """
            {0}pop eax
            cmp eax, 0
            je {1}else
            {2}jmp {1}final
            {1}else:
            {3}{1}final:
            """;

    public final String PRINT_STATEMENT_ASM =
            "{0}fn MessageBoxA,0, str$(eax), \"Prokhorchuk Nadiia IO-03\", MB_OK\n";
    public final String WHILE_STATEMENT_ASM = """
            Loop{0}start:
            {1}pop eax
            cmp eax, 0
            je Loop{0}end
            {2}jmp Loop{0}start
            Loop{0}end:
            """;
    public final String EXPRESSION_STATEMENT_ASM = "{0}\n";
    public final String PROCEDURE_ASM = """
            {0} PROC
            {1}
            {0} ENDP
            """;
    public final String FUNCTION_BODY_ARGUMENTS_ASM = "ret {0}\n";
    public final String VAR_EXPRESSION_ASM = "mov eax, dword ptr[ebp{0}] ; {1}\npush eax\n";
    public final String SUM_ASM = "{0}\n{1}\npop eax\npop ecx\nadd eax, ecx\npush eax\n";
    public final String SUBSTRACT_ASM = "{0}\n{1}\npop eax\npop ecx\nsub eax, ecx\npush eax\n";
    public final String DIVIDE_ASM = "{0}\n{1}\npop eax\npop ebx\nxor edx, edx\ndiv ebx\npush eax\n";
    public final String MULTIPLY_ASM = "{0}\n{1}\npop eax\npop ecx\nimul ecx\npush eax\n";
    public final String MODULO_ASM = "{0}\n{1}\npop eax\npop ebx\nxor edx, edx\ndiv ebx\npush edx\n";
    public final String CALL_EXPRESSION_ASM = "invoke {0}\n push eax\n";
    public final String EQUAL_ASM =
            "{0}\n{1}\npop eax\npop ecx\ncmp eax, ecx\nmov eax, 0\nsete al\npush eax\n";
    public final String NOT_EQUAL_ASM =
            "{0}\n{1}\npop eax\npop ecx\ncmp eax, ecx\nmov eax, 0\nsetne al\npush eax\n";
    public final String GREATER_ASM = "{0}\n{1}\npop eax\npop ecx\ncmp ecx, eax\nmov eax, 0\nsetl al\npush eax\n";
    public final String CONDITION_BODY_ASM = "{0}\njmp {1}final\n";
    public final String CONDITION_ELSE_ASM = "{0}else:\n{1}\n";
    public final String VARIABLE_ASM = "push ebp\nmov ebp, esp\nsub esp, {0}\n";
    public final String FUNCTION_BODY_PARAMS_ASM = "add esp, {0}\nmov esp, ebp\npop ebp\n";
    public final String final_ASM = "\tpush {0}\n";
    public final String RETURN_ASM = "{0}\npop eax\nadd esp, {1}\nmov esp, ebp\npop ebp\nret {2}\n";
    public final String CONDITION_IF_ASM = "{0}\npop eax\ncmp eax, 0\nje {1}final\n";
    public final String CONDITION_IF_WITH_ELSE_ASM = "{0}\npop eax\ncmp eax, 0\nje {1}else\n";
    public final String ID_ASM = "{0}final:\n";

    public final String PYTHON_PRINT = "print";
    public final String PYTHON_WHILE = "while";
    public final String PYTHON_IF = "if";
    public final String PYTHON_ELSE = "else";
    public final String PYTHON_RETURN = "return";
    public final String PYTHON_FUNCTION_DEFENITION = "def";
}
