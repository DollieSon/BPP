import java.sql.SQLOutput;
import java.util.ArrayList;

public class Parser {
    private ArrayList<Token> tokens;
    private int current = 0;

    public Parser(ArrayList<Token> tokens) {
        this.tokens = tokens;
    }

    public ProgramNode parse() {
        ProgramNode program = new ProgramNode();
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
                parseAssignment();
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

    private VariableDeclarationNode parseVariableDeclaration() {
        VariableDeclarationNode declNode = new VariableDeclarationNode();
        declNode.type = advance().token; // NUMERO, LETRA, etc.

        do {
            VariableNode varNode = new VariableNode();
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

    private PrintNode parsePrintStatement() {
        PrintNode printNode = new PrintNode();

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

    private void parseAssignment() {
//        // Step 1: Start with a variable
//        Token varToken = advance(); // consume variable
//        if (!match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
//            error("Expected '=' after variable name for assignment");
//            return;
//        }
//
//        // Step 2: Support chaining (e.g., x = y = 4)
//        ArrayList<String> chain = new ArrayList<>();
//        chain.add(varToken.keyword);
//
//        // Keep processing if there's another VARIABLE and EQUAL_ASSIGN
//        while (match(Tokenizer.Token_Enum.VARIABLE_NAME) && match(Tokenizer.Token_Enum.EQUAL_ASSIGN)) {
//            chain.add(previous().keyword);
//        }
//
//        // Step 3: Expect a value at the end
//        if (match(Tokenizer.Token_Enum.INT_TYPE) ||
//                match(Tokenizer.Token_Enum.CHAR_TYPE) ||
//                match(Tokenizer.Token_Enum.FLOAT_TYPE) ||
//                match(Tokenizer.Token_Enum.BOOL_TYPE) ||
//                match(Tokenizer.Token_Enum.VARIABLE_NAME)) {
//
//            String finalValue = previous().keyword;
//            System.out.println("Variable Assignment: " + String.join(" = ", chain) + " = " + finalValue);
//        } else {
//            error("Expected a literal or variable after '='");
//        }
        processStatement();
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

    public void setTokens(ArrayList<Token> newTokens) {
        this.tokens = newTokens;
        this.current = 0;
    }

    private void error(String message) {
        System.err.println("Parsing error: " + message);
        System.exit(1);
    }
}