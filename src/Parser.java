import java.util.ArrayList;
import java.util.List;

public class Parser {
    private ArrayList<Token> tokens;
    private int current = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public N_ProgramNode parse() {
        N_ProgramNode program = new N_ProgramNode();
        if (!isAtStart(Tokenizer.Token_Enum.PROGRAM_START)) {
            //Maybe static var para ma change change ra ang word na sugod and katapusan and stuff
            error("Expected SUGOD at the beginning");
        }
        System.out.println("Program Start: SUGOD");
        processStatement();
        // Processing sa tunga tunga
        while (!isAtEnd() && !check(Tokenizer.Token_Enum.PROGRAM_END)) {
            // AHAHHAHAHA BRO MADE A MATCH STATEMENT ICANT
            if (match(Tokenizer.Token_Enum.VAR_DECLARE)) { //MUGNA
                System.out.println("1");
                program.statements.add(parseVariableDeclaration());
            } else if (match(Tokenizer.Token_Enum.PRINT_FUNC)) {
                System.out.println("2");
                program.statements.add(parsePrintStatement());
            } else if (check(Tokenizer.Token_Enum.VARIABLE_NAME)) {
                System.out.println("3");
                program.statements.add(parseAssignment());
            } else {
                System.out.println("99");
                processStatement(); // Placeholder for future statements
            }
        }

        if (!match(Tokenizer.Token_Enum.PROGRAM_END)) {
            error("Expected KATAPUSAN at the end");
        }
        System.out.println("Program End: KATAPUSAN");

        System.out.println("Parsing successful!");

        System.out.println("TREE: " + program);
        return program;
    }

    private N_VariableDeclarationNode parseVariableDeclaration() {
        //MUDAWAT SIYAG NUM VARNAME

        N_VariableDeclarationNode declNode = new N_VariableDeclarationNode();
        declNode.type = advance().token; // NUMERO, LETRA, etc.

        do {
            N_VariableNode varNode = new N_VariableNode();
            varNode.name = advance().keyword; // Variable name (e.g., "x")

            if (match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
                if (isAtEnd()) {
                    error("Expected a value after '='");
                }

                // Handle initialization value
                Token valueToken = advance();
                Object value = valueToken.keyword;

                // Handle quoted/backticked values (e.g., `n`, "OO")
                if (valueToken.token == Tokenizer.Token_Enum.BACK_TICK ||
                        valueToken.token == Tokenizer.Token_Enum.DOUBLE_QOUTE ||
                        valueToken.token == Tokenizer.Token_Enum.SINGLE_QOUTE) {
                    if (isAtEnd()) {
                        error("Unclosed quote/backtick");
                    }

                    Token openingSymbol = valueToken;

                    valueToken = advance(); // Get the actual value (e.g., `n`, `OO`)
                    value = valueToken.keyword;

                    valueToken = openingSymbol;

                    // Skip closing quote/backtick
                    Tokenizer.Token_Enum expectedClosingToken;
                    if (valueToken.token == Tokenizer.Token_Enum.BACK_TICK) {
                        expectedClosingToken = Tokenizer.Token_Enum.BACK_TICK;
                    } else if (valueToken.token == Tokenizer.Token_Enum.SINGLE_QOUTE) {
                        expectedClosingToken = Tokenizer.Token_Enum.SINGLE_QOUTE;
                    } else {
                        expectedClosingToken = Tokenizer.Token_Enum.DOUBLE_QOUTE;
                    }

                    if (!match(expectedClosingToken)) {
                        error("Expected closing " +
                                (expectedClosingToken == Tokenizer.Token_Enum.BACK_TICK ? "backtick" :
                                        expectedClosingToken == Tokenizer.Token_Enum.SINGLE_QOUTE ? "single quote" : "double quote"));
                    }
                }

                varNode.value = value;
                System.out.println("Variable Declaration: [" + varNode.name + "] = " + value);
            } else {
                System.out.println("Variable Declaration: [" + varNode.name + "]");
            }

            declNode.variables.add(varNode);
        } while (match(Tokenizer.Token_Enum.COMMA));

        return declNode;
    }

    private N_PrintNode parsePrintStatement() {
        N_PrintNode printNode = new N_PrintNode();

        System.out.println("CURRENT: " + tokens.get(current).keyword);

        // Skip the colon (if present)
       if (!match(Tokenizer.Token_Enum.COLON)){
           error("Expected a colon after IPAKITA'");
        }

        // Parse expressions separated by '&' (concatenation)
        do {
            if (check(Tokenizer.Token_Enum.VARIABLE_NAME)) {
                Token varToken = advance();
                printNode.addExpression(varToken.keyword);
            }
            else if (check(Tokenizer.Token_Enum.DOUBLE_QOUTE) || check(Tokenizer.Token_Enum.BACK_TICK) || check(Tokenizer.Token_Enum.BRACKET_OPEN)) {
                // Handle string literals (e.g., "last")
                advance(); // Skip opening quote
                Token strToken = advance();
                printNode.addExpression(strToken.keyword);
                advance(); // Skip closing quote
            }
        } while (match(Tokenizer.Token_Enum.CONCAT_OPP)); // Continue if there's '&'

        return printNode;
    }

    private N_AssignmentNode parseAssignment() {
        //ISSUE: DI PA MU ERROR ANG x=4=5

        List<String> variables = new ArrayList<>();
        Object finalValue = null;

        // First variable is mandatory
        Token firstVar = advance();
        variables.add(firstVar.keyword);

        // Handle chained assignments (x = y = 4)
        while (match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
            if (isAtEnd()) {
                error("Expected value after '='");
            }

            // Peek next token
            Token nextToken = tokens.get(current);

            // Case 1: Next is another variable (continue chaining)
            if (nextToken.token == Tokenizer.Token_Enum.VARIABLE_NAME &&
                    !isNumeric(nextToken.keyword)) {  // Skip if it's a number
                Token nextVar = advance();
                variables.add(nextVar.keyword);
            }
            // Case 2: Next is a value (end chaining)
            else {
                finalValue = parseValue();
                break;
            }
        }

        // Build assignments right-to-left (x = (y = 4))
        N_AssignmentNode lastAssignment = new N_AssignmentNode(
                variables.get(variables.size()-1),
                finalValue
        );

        for (int i = variables.size()-2; i >= 0; i--) {
            lastAssignment = new N_AssignmentNode(variables.get(i), lastAssignment);
        }

        // Debug print
        System.out.println("Assignment: " +
                String.join(" = ", variables) + " = " + finalValue);

        return lastAssignment;
    }

    // Helper: Check if a string is numeric
    private boolean isNumeric(String str) {
        return str.matches("-?\\d+(\\.\\d+)?");  // Handles integers and decimals
    }

    private void processStatement() {
//        System.out.println("Processing: " + tokens.get(current).keyword);
//        System.out.println("CURRENT TOKEN: " + tokens.get(current).token + " " + tokens.get(current).keyword);
        current++;
    }

    private boolean match(Tokenizer.Token_Enum expected) {
//        System.out.println("EXPECTED: " + expected);
//        System.out.println("CURRENT: " + tokens.get(current).token);
        if (current < tokens.size() && tokens.get(current).token == expected) {
            processStatement();
            return true;
        }
        return false;
    }

    private boolean isAtStart(Tokenizer.Token_Enum expected) {
        return !tokens.isEmpty() && tokens.get(0).token == expected;
    }

    private boolean isAtEnd() {
        return current >= tokens.size();
    }

    private boolean check(Tokenizer.Token_Enum expected) {
        return !isAtEnd() && tokens.get(current).token == expected;
    }

    private Token previous() {
        return tokens.get(current - 1); // Return the most recently consumed token
    }

    private Token advance() {
        if (!isAtEnd()) processStatement();  // Move to the next token
        return previous();  // Return the token we just moved past
    }

    private Object parseValue() {
        Token token = advance();

        // Handle quoted values
        if (token.token == Tokenizer.Token_Enum.BACK_TICK ||
                token.token == Tokenizer.Token_Enum.DOUBLE_QOUTE) {

            Token valueToken = advance(); // Get actual value
            advance(); // Skip closing quote
            return valueToken.keyword;
        }

        // Handle plain values (numbers, variables)
        return token.keyword;
    }

    public void setTokens(ArrayList<Token> newTokens) {
        this.tokens = newTokens;
        this.current = 0;
    }

    private void error(String message) {
        System.err.println("Parsing error: " + message);
        System.exit(1);
    }
}